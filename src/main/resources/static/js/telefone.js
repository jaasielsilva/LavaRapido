document.addEventListener("DOMContentLoaded", function () {

    const input = document.getElementById("telefone");
    if (!input) return;

    input.addEventListener("input", function () {

        let valor = this.value.replace(/\D/g, "");

        // Limita a 11 números
        if (valor.length > 11) {
            valor = valor.substring(0, 11);
        }

        // CELULAR (11 dígitos)
        if (valor.length === 11) {
            valor = valor.replace(/^(\d{2})(\d{5})(\d{4})$/, "($1) $2-$3");
        }
        // FIXO (10 dígitos)
        else if (valor.length === 10) {
            valor = valor.replace(/^(\d{2})(\d{4})(\d{4})$/, "($1) $2-$3");
        }
        // PARCIAL
        else if (valor.length > 2) {
            valor = valor.replace(/^(\d{2})(\d+)/, "($1) $2");
        }
        else if (valor.length > 0) {
            valor = valor.replace(/^(\d+)/, "($1");
        }

        this.value = valor;
    });

    console.log("✔ Máscara de telefone carregada corretamente");
});
