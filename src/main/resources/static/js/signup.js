import {END_POINT} from "./constants.js";

(() => {
    function validateusername(username) {
        let re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(username);
    }

    function validatePassword(password) {
        return password.length >= 6;
    }

    function formatTime(expiredAt) {
        const now = new Date();
        const expiryDate = new Date(expiredAt);
        const diffInSeconds = Math.floor((expiryDate - now) / 1000);

        if (diffInSeconds < 0) {
            return "0분 0초";
        }

        const minutes = Math.floor(diffInSeconds / 60);
        const seconds = diffInSeconds % 60;

        return minutes + "분 " + seconds + "초";
    }

    document.addEventListener('DOMContentLoaded', function () {
        const usernameInput = document.getElementById('username-input');
        const checkBox = document.getElementById('agree');
        const sendusernameButton = document.getElementById('send-username-button');
        const usernameError = document.getElementById('username-error');
        const authSection = document.getElementById('auth-section');
        const commentLeftTime = document.getElementById("comment-left-time");
        const resendButton = document.getElementById("resend-button");
        const sendComment = document.getElementById("send-comment");
        const authInput = document.getElementById("auth-input");
        const authButton = document.getElementById("auth-button");
        const passwordInput = document.getElementById('password-input');
        const passwordError = document.getElementById('password-error');

        const csrfToken = document.body.getAttribute('data-csrf-token');

        sendusernameButton.disabled = true;
        authButton.disabled = true;

        function updateButtonState() {
            const result = !(validateusername(usernameInput.value) && validatePassword(passwordInput.value) && checkBox.checked);

            sendusernameButton.disabled = result;
            authButton.disabled = result;
        }

        const sendVerificationCode = async (username, password, verificationCode) => {

            const response = await fetch(END_POINT + '/public/user/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                    verificationCode: verificationCode,
                    major: 1
                })
            });

            if (response.ok) {
                const responseData = await response.json();
                window.location.href = responseData.redirectUrl; // 서버에서 전송한 URL로 리다이렉션
            } else {
                const reason = await response.text();
                console.log(reason);
                alert(reason);
            }
        }

        let interval;

        const sendVerificationEmail = async (username, recaptchaToken) => {
            const response = await fetch(END_POINT + '/public/emails/verification-requests', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // 'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    username: username,
                    recaptchaToken: recaptchaToken
                })
            });

            if (response.ok) {

                sendusernameButton.style.display = 'none';
                authSection.style.display = 'block';
                authButton.style.display = 'block';
                usernameInput.disabled = true;

                const data = await response.json();

                const expiredAt = data.expiredAt;
                const usernameSent = data.username;

                if (interval) {
                    clearInterval(interval);
                }

                // 1초마다 남은 시간 갱신
                interval = setInterval(() => {
                    const remainingTime = formatTime(expiredAt);

                    commentLeftTime.textContent = "남은 시간: " + remainingTime;

                    if (remainingTime === "0분 0초") {
                        sendusernameButton.style.display = 'block';
                        authSection.style.display = 'none';
                        authButton.style.display = 'none';
                        usernameInput.disabled = false;
                        clearInterval(interval); // 타이머 종료
                    }
                }, 1000);

                //성공시 재전송 가능하게
                resendButton.onclick = async () => {
                    await sendVerificationEmail(usernameSent);
                    alert("메일이 재전송되었습니다.");
                };

                //5초 후 이메일 재전송 안내 문구
                setTimeout(() => {
                    sendComment.style.display = "block";
                }, 5000);

            } else {
                const reason = await response.text();
                alert(reason);
            }
        }


        usernameInput.oninput = (e) => {
            if (e.target.value.length > 0 && !validateusername(e.target.value)) {
                usernameError.style.display = 'block';
            } else {
                usernameError.style.display = 'none';
            }
            updateButtonState();
        };

        passwordInput.oninput = (e) => {
            if (e.target.value.length >= 6) {
                passwordError.style.display = 'none';
            } else {
                passwordError.style.display = 'block';
            }
            updateButtonState();
        };

        authInput.oninput = (e) => {
            if (e.target.value.length > 0) {
                authButton.disabled = false;
            } else {
                authButton.disabled = true;
            }
        }

        checkBox.onchange = updateButtonState;

        sendusernameButton.onclick = async (e) => {
            e.preventDefault();
            grecaptcha.ready(async ()=> {
                const recaptchaToken = await grecaptcha.execute('6LfSRTQpAAAAAF0XXLajxXQFhFXgQytX67bSINky', {action: 'SEND_CODE'});

                await sendVerificationEmail(usernameInput.value, recaptchaToken);
            });

        }

        authButton.onclick = async (e) => {
            e.preventDefault();

            await sendVerificationCode(usernameInput.value, passwordInput.value, authInput.value);
        }
    });

})();