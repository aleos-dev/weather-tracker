document.querySelectorAll(".location-name").forEach((element, index) => {
    element.addEventListener("click", function () {
        // Find the corresponding modal and input for this location
        const modal = document.querySelectorAll(".customModal")[index];
        const locationNameInput = modal.querySelector(".locationNameInput");

        // Set the current location name in the input field
        locationNameInput.value = element.innerText;

        // Open the corresponding modal
        modal.style.display = "block";

        // Close modal when "Cancel" or "X" is clicked
        modal.querySelector(".close").addEventListener("click", () => modal.style.display = "none");
        modal.querySelector(".cancelBtn").addEventListener("click", () => modal.style.display = "none");

        // Save the new location name and close the modal
        modal.querySelector(".saveLocationName").addEventListener("click", function () {
            const newLocationName = locationNameInput.value;

            // Update the location name displayed
            element.innerText = newLocationName;

            // Close the modal
            modal.style.display = "none";

            let formData = new FormData();
            formData.append("_method", "PATCH");
            formData.append("lat", document.getElementsByName("lat")[index].value);
            formData.append("lon", document.getElementsByName("lon")[index].value);
            formData.append("locationName", newLocationName);

            // Send a POST_PATCH request
            fetch('/api/v1/locations', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        console.log('Location name updated successfully');
                    } else {
                        console.error('Failed to update location');
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    });
});

// Close the modal if the user clicks outside of it
window.onclick = function (event) {
    document.querySelectorAll(".customModal").forEach(modal => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
};
