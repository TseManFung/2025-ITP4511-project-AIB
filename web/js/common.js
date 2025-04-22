// scroll
function scrollTo(elementID) {
  targetElement = document.getElementById(elementID);

  targetElement.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });

}

function modalSetTitle(title) {
  $("#main-modal-title").text(title);
}

function modalLoadBody(path) {
  $("#main-modal-body").load(path);
}

function modalSetBody(body) {
  $("#main-modal-body").html(body);
}
function showModal() {
  $("#main-modal").modal("show");
}

function hideModal() {
  $("#main-modal").modal("hide");
}

function modelEnableStatic() {
  $("#main-modal").attr({
    "data-bs-backdrop": "static",
    "data-bs-keyboard": "false",
    "aria-labelledby": "staticBackdropLabel",
    "aria-hidden": "true",
  });
}

function modalDisableStatic() {
  $("#main-modal").removeAttr("data-bs-backdrop");
  $("#main-modal").removeAttr("data-bs-keyboard");
  $("#main-modal").removeAttr("aria-labelledby");
  $("#main-modal").removeAttr("aria-hidden");
}

function modalSetCloseBtn(Show = true, action = null) {
  if (Show) {
    $("#main-modal-close").click(action);
    $("#main-modal-close").show();
  } else {
    $("#main-modal-close").hide();
  }
}

function modalSetPrimaryBtn(Show = true, text = "Confirm", action = null) {
  if (Show) {
    $("#main-modal-primary").text(text);
    $("#main-modal-primary").click(action);
    $("#main-modal-primary").show();
  } else {
    $("#main-modal-primary").hide();
  }
}

function modalSetSecondaryBtn(Show = true, text = "Cancel", action = null) {
  if (Show) {
    $("#main-modal-secondary").text(text);
    $("#main-modal-secondary").click(action);
    $("#main-modal-secondary").show();
  } else {
    $("#main-modal-secondary").hide();
  }
}

// page top
document.addEventListener("DOMContentLoaded", function () {
  topBtn = document.querySelector("#page-top");
  if (topBtn === null) {
    return;
  }
  topBtn.style.display = "none";

  window.addEventListener("scroll", function () {
    if (window.scrollY > 350) {
      topBtn.style.display = "block";
    } else {
      topBtn.style.display = "none";
    }
  });

  topBtn.addEventListener("click", function () {
    scrollTo("header-wrap");
    return false;
  });
});

// page
function resizeIframe(obj) {
  obj.style.height = obj.contentWindow.document.body.scrollHeight + "px";
}

// get parameter from URL
function getParameterFromURL(parameterName) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(parameterName);
}

// go back to previous page
function goBack() {
  window.history.back();
}

function addToCart1(itemID) {
  $.ajax({
    type: "POST",
    url: "./addToCart.php",
    data: {
      sparePartNum: itemID,
      qty: 1,
    },
    success: function (data) {
      location.reload();
    },
  });
}

function addToCart(itemID, quantity) {
  $.ajax({
    type: "POST",
    url: "./addToCart.php",
    data: {
      sparePartNum: itemID,
      qty: quantity,
    },
    success: function (data) {
      window.location.href = "./dealer_cart.php";
    },
  });
}

function GoToPage_POST(action, kvPair) {
  var form = document.createElement("form");
  form.style.visibility = "hidden"; // no user interaction is necessary
  form.method = "POST"; // forms by default use GET query strings
  form.action = action;
  obj = Object.keys(kvPair);
  for (key in obj) {
    var input = document.createElement("input");
    input.name = obj[key];
    input.value = kvPair[obj[key]];
    form.appendChild(input); // add key/value pair to form
  }
  document.body.appendChild(form); // forms cannot be submitted outside of body
  form.submit();
}


function getCookie(cname) {
  let name = cname + "=";
  let decodedCookie = decodeURIComponent(document.cookie);
  let ca = decodedCookie.split(";");
  for (let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == " ") {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}
function setCookie(cname, cvalue, exdays) {
  const d = new Date();
  d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
  let expires = "expires=" + d.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
function itemsSetCell() {
  $("#items").removeClass("list");
  $("#items").addClass("cell");
  setCookie("itemsView", "cell", 365);
}
function itemsSetList() {
  $("#items").removeClass("cell");
  $("#items").addClass("list");
  setCookie("itemsView", "list", 365);
}

async function insertScript(src, callback = null) {
  script = document.createElement("script");
  script.src = src;
  script.onload = callback;
  document.head.appendChild(script);
  await new Promise((resolve) => script.onload = resolve);
}

function readJsonFile(file, callback) {
  fetch(file)
    .then((response) => {
      if (!response.ok) {
        // If the file is not found, create it
        if (response.status === 404) {
          return {};
        }
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then((data) => callback(data))
    .catch((error) => console.error('There was a problem with the fetch operation:', error));
}

function checkInputIn(elementID, warningClass = "") {
  const element = document.getElementById(elementID);
  let emptyInput = [];

  element.querySelectorAll("input[required]").forEach((input) => {
    if (!input.value || input.value.trim() === "") {
      emptyInput.push(input);
      input.classList.add(warningClass);
    }
  });

  return emptyInput;
}

if (!String.format) {
  String.format = function (format) {
    var args = Array.prototype.slice.call(arguments, 1);
    return format.replace(/{(\d+)}/g, function (match, number) {
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
        ;
    });
  };
}