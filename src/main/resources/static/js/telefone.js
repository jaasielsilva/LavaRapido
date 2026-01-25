document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("telefone");
    if (!input) return;

    function formatarTelefone(valor) {
        let numeros = valor.replace(/\D/g, "");

        // Limita a 11 dígitos (celular)
        if (numeros.length > 11) numeros = numeros.substring(0, 11);

        if (numeros.length > 10) {
            return numeros.replace(/^(\d{2})(\d{5})(\d{4})$/, "($1) $2-$3"); // celular
        } else if (numeros.length > 6) {
            return numeros.replace(/^(\d{2})(\d{4})(\d{0,4})$/, "($1) $2-$3"); // fixo
        } else if (numeros.length > 2) {
            return numeros.replace(/^(\d{2})(\d*)$/, "($1) $2");
        } else if (numeros.length > 0) {
            return "(" + numeros;
        }
        return "";
    }

    function aplicarMascara(event) {
        const cursorPos = input.selectionStart;
        const valorAntigo = input.value;

        input.value = formatarTelefone(input.value);

        // Ajusta o cursor para não pular
        let diff = input.value.length - valorAntigo.length;
        let novaPos = cursorPos + diff;
        if (novaPos < 0) novaPos = 0;
        input.setSelectionRange(novaPos, novaPos);
    }

    input.addEventListener("input", aplicarMascara);

    // Permitir apenas números, backspace, delete e setas
    input.addEventListener("keydown", function(e) {
        const permitido = ["Backspace","Delete","ArrowLeft","ArrowRight","ArrowUp","ArrowDown","Tab"];
        if (!/\d/.test(e.key) && !permitido.includes(e.key) && !(e.ctrlKey || e.metaKey)) {
            e.preventDefault();
        }
    });

    aplicarMascara(); // aplica máscara se já houver valor
});
