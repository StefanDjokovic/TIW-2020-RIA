(function () {

    var albumList, current_pictureDetails, draggables, containers,
        pageOrchestrator = new pageOrchestrator(); // main controller

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "login.html";
        } else {
            pageOrchestrator.start();
            pageOrchestrator.refresh();
        }
    }, false)

    // contains functions and details to display the albums
    function AlbumList(listcontainer, listcontainerbody, albumDetails) {
        this.listcontainer = listcontainer;
        this.listcontainerbody = listcontainerbody;
        this.albumDetails = albumDetails;

        // resets the listcontainer so it is not visible while loading
        this.hide = function () {
            this.listcontainer.style.visibility = "hidden";
        }

        // GET request to the server for the album list
        this.getAlbumList = function () {
            var self = this;
            makeCall("GET", "GoToAlbumPage", null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var albums = JSON.parse(req.responseText);
                            if (albums.length == 0) {
                                self.alert.textContent = "No Albums";
                                return;
                            }
                            self.update(albums)
                        }
                    }
                }
            );
        };

        // Updates the AlbumList with the data recieved
        this.update = function (arrayAlbums) {
            arrayAlbums.forEach(function (album) {
                let div = document.createElement("div");
                anchor = document.createElement("a");
                div.setAttribute("class", "albumContainer");
                div.setAttribute("draggable", true);
                anchor.textContent = album.name;
                anchor.setAttribute("album_id", album.id);
                anchor.addEventListener("click", (e) => {
                    // I HAVE TO SHOW THE ALBUM DETAILS
                    albumDetails.closeModal();
                    albumDetails.show(e.target.getAttribute("album_id"));
                }, false);
                anchor.href = "#";
                div.append(anchor)
                listcontainerbody.append(div);
            });
            document.getElementById("saveChanges").disabled = "disabled";

            // Here the drag logic, code in part from https://www.youtube.com/watch?v=jfYWwQrtzzY
            draggables = document.querySelectorAll('.albumContainer');
            containers = document.querySelectorAll('.albumbody');

            draggables.forEach(draggable => {
                draggable.addEventListener('dragstart', () => {
                    draggable.classList.add('dragging')
                })

                draggable.addEventListener('dragend', () => {
                    draggable.classList.remove('dragging')
                })
            })

            containers.forEach(container => {
                container.addEventListener('dragover', e => {
                    e.preventDefault()
                    const afterElement = getDragAfterElement(container, e.clientY)
                    const draggable = document.querySelector('.dragging')
                    if (afterElement == null) {
                        container.appendChild(draggable)
                    } else {
                        container.insertBefore(draggable, afterElement)
                    }
                    document.getElementById("saveChanges").removeAttribute("disabled");
                    document.getElementById("saveChanges").removeEventListener("click", this.saveChangesOrder, false);
                    document.getElementById("saveChanges").addEventListener("click", this.saveChangesOrder, false);
                    console.log("dragging woho!");
                })
            })

            function getDragAfterElement(container, y) {
                const draggableElements = [...container.querySelectorAll('.albumContainer:not(.dragging)')]

                return draggableElements.reduce((closest, child) => {
                    const box = child.getBoundingClientRect()
                    const offset = y - box.top - box.height / 2
                    if (offset < 0 && offset > closest.offset) {
                        return { offset: offset, element: child }
                    } else {
                        return closest
                    }
                }, { offset: Number.NEGATIVE_INFINITY }).element
            }
            
            // Everything is now loaded, making it visible
            this.listcontainer.style.visibility = "visible";
        }

        // Send the new order to the server 
        this.saveChangesOrder = function () {
            let list = document.getElementById("albumbody");
            console.log(list);

            let albHTML = list.getElementsByTagName("a");
            console.log(albHTML)
            let formData = [];
            
            let alb = Array.from(albHTML);
            let i;
            for (i = 0; i < alb.length; i++) {
                let data = {pos: i, album_id: alb[i].getAttribute("album_id")};
                formData.push(data);
            }

            console.log(formData);

            $.ajax({
                type: "POST",
                url: 'SaveOrderChanges',
                data: JSON.stringify(formData),
                success: function (response) {
                    console.log("I have received some comments correctly!");
                    document.getElementById("saveChanges").setAttribute("disabled", true);
                },
                error: function (req) {
                    console.log("Check if DB has the element");

                }
            });
        }
    };

    // contains functions and details to display the single album
    function AlbumDetails(options) {
        this.table = options['table'];
        this.titles = options['titles'];
        this.tdImages = options['tdImages']
        this.left_nav = options['left_nav']
        this.right_nav = options['right_nav']

        var self = this;

        this.show = function (album_id) {

            makeCall("GET", "GoToSingleAlbumView?album_id=" + album_id, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var pictures = JSON.parse(req.responseText);
                            if (pictures == null || pictures === null)
                                console.log("Aaaaaaah");
                            self.updateNewAlbum(pictures);
                        }
                        else {
                            console.log("what");
                        }

                    }
                });

        };

        // Activated when a different album has been pressed
        var album_page = 0;
        this.updateNewAlbum = function (pictureDetails) {
            current_pictureDetails = pictureDetails;
            album_page = 0;
            self.update();
        }

        // Checks if the left and right button should be active or not
        this.validate_go = function () {
            right_nav.setAttribute("hidden", true);
            right_nav.removeEventListener("click", this.right_go, false);
            if (album_page * 5 + 5 < current_pictureDetails.length) {
                right_nav.addEventListener("click", this.right_go, false);
                right_nav.removeAttribute("hidden");
            }

            left_nav.setAttribute("hidden", true);
            left_nav.removeEventListener("click", this.left_go, false);
            if (album_page > 0) {
                left_nav.addEventListener("click", this.left_go, false);
                left_nav.removeAttribute("hidden");
            }
        }

        // the right button has been pressed, so the update will be called on the
        // following page
        this.right_go = function (e) {
            album_page += 1;
            self.closeModal();
            self.update();
        }

        // the left button has been pressed, so the update will be called on the
        // previous album page
        this.left_go = function (e) {
            album_page -= 1;
            self.closeModal();
            self.update();
        }

        // updates the data and view for AlbumDetails
        this.update = function () {

            // first hide the previous resoult
            console.log("Im in update")
            for (var i = 0; i < 5; i++) {
                this.titles[i].style.display = "none";
                this.tdImages[i].style.display = "none";
                this.tdImages[i].removeEventListener('mouseenter', self.openModal, false);
                this.tdImages[i].removeAttribute("pic_id");
                this.tdImages[i].removeAttribute("descr");
            }

            // check if the right and left button should be available
            self.validate_go();

            // then add the pictures in the right filed
            if (current_pictureDetails.length <= album_page * 5) {
                this.table.style.display = "none";
                document.getElementById("emptyMessage").textContent = "This album is empty";
            }
            else {
                this.table.removeAttribute("style");
                document.getElementById("emptyMessage").textContent = "";
            }

            // sets the title, attributes and images in the table
            for (var i = album_page * 5; i < album_page * 5 + 5 && current_pictureDetails.length > i; i++) {
                this.titles[i - album_page * 5].removeAttribute("style");
                this.tdImages[i - album_page * 5].removeAttribute("style");

                if (current_pictureDetails[i].title != null) {
                    this.titles[i - album_page * 5].textContent = current_pictureDetails[i].title;
                    this.tdImages[i - album_page * 5].setAttribute("title", current_pictureDetails[i].title);
                }
                else {
                    this.titles[i - album_page * 5].textContent = "No Title";
                    this.tdImages[i - album_page * 5].setAttribute("title", "No Title");

                }
                this.tdImages[i - album_page * 5].getElementsByTagName("img")[0].src = current_pictureDetails[i].filepath;
                this.tdImages[i - album_page * 5].setAttribute("pic_id", current_pictureDetails[i].picture_id);
                this.tdImages[i - album_page * 5].setAttribute("descr", current_pictureDetails[i].descr);
                this.tdImages[i - album_page * 5].setAttribute("filepath", current_pictureDetails[i].filepath);


                this.tdImages[i - album_page * 5].addEventListener('mouseenter', self.openModal, false);
            }
        };

        // opens the modal that contains the selected picture, image and informations
        this.openModal = function (event) {
            console.log(event.target)
            console.log(event.target.getAttribute("pic_id"));
            console.log("OPEN MODAL");
            modal = document.getElementById("modal");
            if (modal == null) return;
            modal.classList.add('active')
            document.getElementById('closeButton').addEventListener('click', () => {
                self.closeModal();
            }, false);

            self.callUpdateModal(event, event.target.getAttribute("pic_id"));
            self.commentButton(event.target.getAttribute("pic_id"));
        }

        // closes the modal
        this.closeModal = function () {
            modal = document.getElementById("modal");
            if (modal == null) return;
            modal.classList.remove('active')
        }

        // event triggered to update the modal with the information and comments 
        this.callUpdateModal = function (event, pic_id) {
            makeCall("GET", "OpenInfo?pic_id=" + pic_id, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var comments = JSON.parse(req.responseText);
                            if (comments == null || comments === null)
                                console.log("Aaaaaaah");

                            self.updateModal(event.target.getAttribute("title"), event.target.getAttribute("filepath"), event.target.getAttribute("descr"));
                            self.updateComments(comments);
                        }
                        else {
                            console.log("what");
                        }

                    }
                });
        }

        // requests the comments from the selected picture
        this.callUpdateComments = function (pic_id) {
            makeCall("GET", "OpenInfo?pic_id=" + pic_id, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var comments = JSON.parse(req.responseText);
                            if (comments == null || comments === null)
                                console.log("Aaaaaaah");
                            self.updateComments(comments);
                        }
                        else {
                            console.log("what");
                        }

                    }
                });
        }

        // updates the informations of the displayed informations of the modal 
        this.updateModal = function (title, filepath, descr) {
            document.getElementById("modaltitle").textContent = title;
            document.getElementById("modal").getElementsByTagName("img")[0].onload = function() {
                document.getElementById("modal").setAttribute("width", (this.width + 20) + "px");
                document.getElementById("modal").style.width = (this.width + 20) + "px";
                console.log(this.width + ' x ' + this.height);
            }
            document.getElementById("modal").getElementsByTagName("img")[0].src = filepath;
            console.log(descr)
            if (descr == null || descr == "undefined")
                document.getElementById("modaldescr").textContent = "No description";
            else 
                document.getElementById("modaldescr").textContent = descr;

        }

        // updates the comments recieved with callUpdateComments
        this.updateComments = function (comments) {
            console.log(comments)
            comdiv = document.getElementById("modalcomments");
            comdiv.innerHTML = "";

            comments.forEach(function (comment) {
                console.log(comment)
                username = document.createElement("div");
                username.classList.add("user_name");
                username.textContent = comment.username;
                text = document.createElement("div");
                text.classList.add("comment_body")
                text.textContent = comment.comment;
                username.append(text);
                comdiv.append(username);
            });
        }

        // called when the comment button has been pressed
        var curr_picid;
        this.commentButton = function (pic_id) {
            console.log("The comment button is now activated!")
            document.getElementById("submitComment").removeEventListener('click', self.commentButtonFunction, false);
            curr_picid = pic_id;
            document.getElementById("submitComment").addEventListener('click', self.commentButtonFunction, false);
        }

        // Makes a POST request with the comment submitted by the user
        this.commentButtonFunction = function (e) {
            console.log("The comment button has been pressed!");
            let comment = document.getElementById("newcomment").value;
            console.log("comment: " + comment);
            // Comment accepted if the comment is not empty and if it's of no more than 280 characters
            if (comment.trim().length > 0 && comment.length <= 280) {
                // Sending the pic id, the username, and the comment; there is also a check server side about these fields
                let formData = { pic_id: curr_picid, username: sessionStorage.getItem("username"), comment: comment };
                console.log(curr_picid + " " + sessionStorage.getItem("username") + " " + comment)
                $.ajax({
                    type: "POST",
                    url: 'AddComment',
                    data: JSON.stringify(formData),
                    success: function (response) {
                        console.log("I have received some comments correctly!");
                        document.getElementById("newcomment").value = '';
                        // Requesting the comments update to see the comment that was added
                        self.callUpdateComments(curr_picid);
                    },
                    error: function (req) {
                        document.getElementById("errorSubmitComment").textContent = "Error in the request"
                    },
                    dataType: "json"
                });
            }
            else {
                console.log("comment was empty or too long")
                let errorSubmitComment = document.getElementById("errorSubmitComment");
                if (comment.trim().length <= 0)
                    errorSubmitComment.textContent = "Empty messages are not accepted";
                else 
                    errorSubmitComment.textContent = "Comment can't be of more than 280 characters";
            }

        }


    };


    function pageOrchestrator() {
        this.start = function () {
            // set username for the welcome message
            document.getElementById("id_username").textContent = sessionStorage.getItem("username");    

            // contains functions and details to display the single album
            albumDetails = new AlbumDetails({
                table: document.getElementById("albumview"),
                titles: document.getElementById("titles").getElementsByTagName("th"),
                tdImages: document.getElementById("images").getElementsByTagName("td"),
                left_nav: document.getElementById("left_nav"),
                right_nav: document.getElementById("right_nav"),
            });

            // contains functions and details to display the albums
            albumList = new AlbumList(
                document.getElementById("albums"),
                document.getElementById("albumbody"),
                albumDetails
            )
        }
        // initial setup functions
        this.refresh = function () {
            albumList.hide();
            albumList.getAlbumList();

        }
    }



})();