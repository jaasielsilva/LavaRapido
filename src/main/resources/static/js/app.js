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

