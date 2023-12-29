import {END_POINT} from "./constants.js";

(()=>{

    function validateusername(username) {
        let re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(username);
    }


    document.addEventListener('DOMContentLoaded', ()=>{
        const loginButton = document.getElementById('login-button');

        const loginError = document.getElementById('login-error');

        const usernameInput = document.getElementById('username-input');

        const passwordInput = document.getElementById('password-input');

        const csrfToken = document.body.getAttribute('data-csrf-token');


        loginButton.disabled = true;

        loginButton.onclick = async (e) => {
            e.preventDefault();

            const username = usernameInput.value;
            const password = passwordInput.value;

            const response = await fetch(END_POINT + '/login-request', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            if(response.ok) {
                window.location.href = '/mypage';
            } else {
                loginError.style.display = 'block';
                console.log(await response.text());
                // alert(await response.text());
            }
        };

        usernameInput.oninput = (e) => {
            updateLoginButtonState();
        };

        passwordInput.oninput = (e) => {
            updateLoginButtonState();
        };

        const updateLoginButtonState = () => {
            if(validateusername(usernameInput.value) && passwordInput.value.length > 0) {
                loginButton.disabled = false;
            } else {
                loginButton.disabled = true;
            }
        };


    });
})();