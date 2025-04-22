document.addEventListener("DOMContentLoaded", function () {
    const body = document.body;
    const eye = $(".fa-regular");
    const beam = document.querySelector(".beam");
    const btnLogin = document.getElementsByClassName("btn-login")[0];
    const btnNext = document.getElementsByClassName("btn-next")[0];

    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("redirect")) {
        const otherLink = $("#other").attr("href");
        const redirect = otherLink + "?redirect=" + encodeURIComponent(urlParams.get("redirect"));
        $("#other").attr("href", redirect);
    }
    body.addEventListener("mousemove", function (e) {
        let rect = beam.getBoundingClientRect();
        let mouseX = rect.right + rect.width / 2;
        let mouseY = rect.top + rect.height / 2;
        let rad = Math.atan2(mouseX - e.pageX, mouseY - e.pageY);
        let deg = rad * (20 / Math.PI) * -1 - 350;
        body.style.setProperty("--beam-deg", deg + "deg");
    });

    eye.bind("click", function (e) {
        e.preventDefault();
        const ID = this.getAttribute("for");
        const passwordInput = document.getElementById(ID);
        this.parentElement.classList.toggle("show-beam");
        if (!body.classList.contains("show-password")) {
            body.classList.add("show-password");
        } else if (document.querySelectorAll(".ipt-box.show-beam").length === 0) {
            body.classList.remove("show-password");
        }
        passwordInput.type =
                passwordInput.type === "password" ? "text" : "password";
        this.className =
                "fa-regular " +
                (passwordInput.type === "password" ? "fa-eye-slash" : "fa-eye");
        passwordInput.focus();
    });



    if (btnNext !== undefined) {
        $("#backBtn").click(function (e) {
            const current = btnNext.getAttribute("curr");
            const finish = btnNext.getAttribute("fin");
            const next = parseInt(current, 10) - 1;
            if (next < 0) {
                goBack();
                return;
            }
            if (current == finish) {
                btnNext.innerText = "Next";
            }
            btnNext.setAttribute("curr", next);
            $("#card-" + current).addClass("d-none");
            $("#card-" + next).removeClass("d-none");
        });
        function checkInput(current) {
            inputs = document.querySelectorAll("#card-" + current + " .ipt-box");
            let valid = true;
            inputs = Array.from(inputs).filter(
                    (input) => !input.classList.contains("d-none")
            );
            inputs.forEach((ele) => {
                inputEle = ele.getElementsByTagName("input")[0]
                if (ele.id === "radio-group") {
                    checked = false;
                    ele.querySelectorAll("[name='identity']").forEach((radio) => {
                        if (radio.checked) {
                            checked = true;
                        }
                    });
                    if (!checked) {
                        valid = false;
                    }
                } else if (inputEle.value === "") {
                    valid = false;
                    inputEle.classList.add("is-invalid");
                }
                if (inputEle.getAttribute("type") === "email") {
                    const email = inputEle.value;
                    if (!email.includes("@") || !email.includes(".")) {
                        valid = false;
                        inputEle.classList.add("is-invalid");
                    }
                } else if (inputEle.getAttribute("type") === "tel") {
                    const tel = inputEle.value;
                    if (tel.length !== 8 || isNaN(tel)) {
                        valid = false;
                        inputEle.classList.add("is-invalid");
                    }
                }
            });
            if (!valid) {
                modalSetPrimaryBtn((Show = false));
                modalSetTitle("information incomplete");
                modalSetBody("Please fill in all fields correctly");
                showModal();
                return false;
            }
            return true;
        }





    }
});
