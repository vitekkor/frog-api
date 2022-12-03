Array.from(document.getElementsByClassName("input-box")).forEach(inputBox => {
    let input = inputBox.children[0]
    if (input.classList.contains("bad")) {
        input.valid = false
        let popup = getPopup(input)
        if (popup) {
            popup.classList.toggle("show");
        }
    }
    input.onchange = () => {
        input.classList.remove("bad");
    }
    input.onfocus = () => {
        let popup = getPopup(input)
        if (popup && popup.classList.contains("show")) {
            popup.classList.toggle("show");
        }
    }
})

function getPopup(input) {
    let popup
    if (input.id === "email") {
        popup = document.getElementById("emailPopup");
    } else if (input.id === "password") {
        popup = document.getElementById("passwordPopup");
    }
    return popup
}