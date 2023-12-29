(()=>{
    document.addEventListener('DOMContentLoaded', ()=>{
        const modal = document.getElementById('modal');

        const unregisterButton = document.getElementById('unregister-button');

        const logoutButton = document.getElementById('logout-button');

        const cancelButton = document.getElementById('cancel-button');

        const alertOnButton = document.getElementById('alert-on-button');

        const alertOffButton = document.getElementById('alert-off-button');

        const userId = document.body.dataset.userId;


        unregisterButton.onclick = () => {
            modal.style.display = 'flex';
        };

        logoutButton.onclick = () => {

        };

        cancelButton.onclick = () => {
            modal.style.display = 'none';
        };

        alertOnButton.onclick = () => {
            fetch('/api/alert', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                   //TODO
                })
            });
        };

        alertOffButton.onclick = () => {

        };

    });
})();