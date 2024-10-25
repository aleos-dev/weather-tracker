document.addEventListener('DOMContentLoaded', function() {
    const weatherCards = document.querySelectorAll('.weather-card');

    weatherCards.forEach(card => {
        // Store the last request time for trivia
        let lastRequestTime = 0;

        card.addEventListener('mouseover', function() {
            const temperature = card.getAttribute('data-temp');
            const currentTime = Date.now();

            // If 10 seconds have passed since the last request, show trivia
            if (currentTime - lastRequestTime >= 30000) {
                lastRequestTime = currentTime;
                showTrivia(card, temperature);

                // Automatically hide the trivia after 10 seconds
                setTimeout(() => {
                    hideTrivia(card);
                }, 30000);
            }
        });
    });
});

function showTrivia(cardElement, temperature) {
    const triviaPopup = cardElement.querySelector('.trivia-popup');
    const triviaContent = cardElement.querySelector('#trivia-content');

    // Show the trivia popup
    triviaPopup.style.display = 'block';
    triviaContent.textContent = 'Loading trivia...';

    // Fetch trivia based on the temperature
    fetch(`http://numbersapi.com/${temperature}`)
        .then(response => response.text())
        .then(trivia => {
            triviaContent.textContent = trivia;
        })
        .catch(error => {
            console.error('Error fetching trivia:', error);
            triviaContent.textContent = "Couldn't load trivia.";
        });
}

function hideTrivia(cardElement) {
    const triviaPopup = cardElement.querySelector('.trivia-popup');

    // Hide the trivia popup after 10 seconds
    triviaPopup.style.display = 'none';
}
