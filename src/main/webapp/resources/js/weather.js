document.addEventListener('DOMContentLoaded', function() {
    const weatherCards = document.querySelectorAll('.weather-card');

    weatherCards.forEach(card => {
        // Store the last request time for trivia
        let lastRequestTime = 0;

        card.addEventListener('mouseover', function() {
            const temperature = card.getAttribute('data-temp');
            const currentTime = Date.now();

            // If 30 seconds have passed since the last request, show trivia
            if (currentTime - lastRequestTime >= 30000) {
                lastRequestTime = currentTime;
                showTrivia(card, temperature);

                // Automatically hide the trivia after 10 seconds
                setTimeout(() => {
                    hideTrivia(card);
                }, 10000); // 10 seconds for visibility
            }
        });
    });
});

function showTrivia(cardElement, temperature) {
    const triviaPopup = cardElement.querySelector('.trivia-popup');
    const triviaContent = cardElement.querySelector('.trivia-content');

    if (triviaPopup && triviaContent) {
        // Show the trivia popup
        triviaPopup.style.display = 'block';
        triviaContent.textContent = '';

        // Fetch trivia based on the temperature
        // fetch(`https://numbersapi.com/${temperature}`)
        fetch('https://uselessfacts.jsph.pl/random.json?language=en')
            .then(response => response.json())
            .then(trivia => {
                triviaContent.textContent = trivia.text;
            })
            .catch(error => {
                console.error('Error fetching trivia:', error);
                triviaContent.textContent = "";
            });
    }
}

function hideTrivia(cardElement) {
    const triviaPopup = cardElement.querySelector('.trivia-popup');

    // Hide the trivia popup
    if (triviaPopup) {
        triviaPopup.style.display = 'none';
    }
}
