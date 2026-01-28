window.ljConfirm = function (options) {
  var title = options && options.title ? options.title : "Tem certeza?";
  var text = options && options.text ? options.text : "Esta ação não poderá ser desfeita.";
  var confirmButtonText = options && options.confirmButtonText ? options.confirmButtonText : "Confirmar";
  var cancelButtonText = options && options.cancelButtonText ? options.cancelButtonText : "Cancelar";

  return Swal.fire({
    icon: "warning",
    title: title,
    text: text,
    showCancelButton: true,
    confirmButtonText: confirmButtonText,
    cancelButtonText: cancelButtonText,
    reverseButtons: true,
  });
};

document.addEventListener("DOMContentLoaded", function () {
  var cnpjInput = document.getElementById("cnpj");
  if (!cnpjInput) return;

  function formatarCnpj(valor) {
    var numeros = valor.replace(/\D/g, "").slice(0, 14);
    var p1 = numeros.slice(0, 2);
    var p2 = numeros.slice(2, 5);
    var p3 = numeros.slice(5, 8);
    var p4 = numeros.slice(8, 12);
    var p5 = numeros.slice(12, 14);
    var r = p1;
    if (p2) r += "." + p2;
    if (p3) r += "." + p3;
    if (p4) r += "/" + p4;
    if (p5) r += "-" + p5;
    return r;
  }

  function aplicarMascara() {
    cnpjInput.value = formatarCnpj(cnpjInput.value);
  }

  cnpjInput.addEventListener("input", aplicarMascara);
  cnpjInput.addEventListener("keydown", function (e) {
    var permitido = ["Backspace", "Delete", "ArrowLeft", "ArrowRight", "ArrowUp", "ArrowDown", "Tab"];
    if (!/\d/.test(e.key) && !permitido.includes(e.key) && !(e.ctrlKey || e.metaKey)) {
      e.preventDefault();
    }
  });
  aplicarMascara();
});

document.addEventListener("DOMContentLoaded", function () {
  var el = document.querySelector(".sidebar-main");
  if (!el) return;
  function u() {
    var h = el.scrollHeight > el.clientHeight;
    el.classList.toggle("has-scroll", h);
    el.classList.toggle("at-top", el.scrollTop <= 1);
    el.classList.toggle("at-bottom", el.scrollTop >= el.scrollHeight - el.clientHeight - 1);
  }
  el.addEventListener("scroll", u);
  window.addEventListener("resize", u);
  u();
});

