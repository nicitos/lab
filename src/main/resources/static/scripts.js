document.addEventListener('DOMContentLoaded', function() {
    // Подтверждение удаления
    const deleteForms = document.querySelectorAll('.delete-form');
    deleteForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!confirm('Удалить город?')) {
                event.preventDefault();
            }
        });
    });

    // Валидация форм для погодных записей
    const weatherForms = document.querySelectorAll('form[id^="weather-form-"]');
    weatherForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            const cloudinessInput = form.querySelector('input[name="cloudiness"]');
            const cloudiness = parseInt(cloudinessInput.value);

            if (isNaN(cloudiness) || cloudiness < 0 || cloudiness > 100) {
                alert('Облачность должна быть в диапазоне от 0 до 100!');
                event.preventDefault();
                return;
            }

            const timestampInput = form.querySelector('input[name="timestamp"]');
            if (!timestampInput.value) {
                alert('Пожалуйста, выберите дату и время!');
                event.preventDefault();
            }
        });
    });
});