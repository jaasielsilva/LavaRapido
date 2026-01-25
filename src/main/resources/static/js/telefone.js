document.addEventListener("DOMContentLoaded", function () {

    console.log("JS telefone carregado");

    const input = document.getElementById("telefone");

    if (!input) {
        console.log("Campo telefone não encontrado");
        return;
    }

    console.log("Campo telefone encontrado");

    input.addEventListener("input", function () {
        let v = this.value.replace(/\D/g, "");

        if (v.length > 11) v = v.slice(0, 11);

        if (v.length > 10)
            v = v.replace(/^(\d{2})(\d{5})(\d{4})$/, "($1) $2-$3");
        else if (v.length > 5)
            v = v.replace(/^(\d{2})(\d{4})(\d{0,4})$/, "($1) $2-$3");
        else if (v.length > 2)
            v = v.replace(/^(\d{2})(\d{0,5})$/, "($1) $2");

        this.value = v;
    });
});