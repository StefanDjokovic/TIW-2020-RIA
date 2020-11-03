/**
 * Login management
 */

(function () { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'CheckLoginInfo', e.target.closest("form"),
        function (req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
                sessionStorage.setItem('username', message);
                window.location.href = "albums.html";
                break;
              case 400: // bad request
                document.getElementById("errorMsg").textContent = message;
                break;
              case 401: // unauthorized
                document.getElementById("errorMsg").textContent = message;
                break;
              case 500: // server error
                document.getElementById("errorMsg").textContent = message;
                break;
            }
          }
        }
      );
    } else {
      form.reportValidity();
    }
  });

  document.getElementById("registerbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      let name = document.getElementById("Rname").value;
      let surname = document.getElementById("Rsurname").value;
      let email = document.getElementById("Remail").value;
      let username = document.getElementById("Rusername").value;
      let password = document.getElementById("Rpassword1").value;
      let rpassword = document.getElementById("Rpassword2").value;
      let formData = { name: name, surname: surname, email: email, username: username, password: password, rpassword: rpassword };
      // regex from https://stackoverflow.com/questions/46155/how-to-validate-an-email-address-in-javascript
      const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      if (password == rpassword && re.test(String(email).toLowerCase())) {
        $.ajax({
          type: "POST",
          url: 'CheckRegistrationInfo',
          data: JSON.stringify(formData),
          success: function (response) {
            document.getElementById("registerMessage").textContent = response.responseText;
          },
          error: function (response) {
            document.getElementById("registerMessage").textContent = response.responseText;
          },
          dataType: "json"
        });
      }
      else {
        if (password != rpassword)
          document.getElementById("registerMessage").textContent = "Passwords don't match!";
        else 
          document.getElementById("registerMessage").textContent = "email is not formatted correctly";
      }

    } else {
      form.reportValidity();
    }
  });

})();