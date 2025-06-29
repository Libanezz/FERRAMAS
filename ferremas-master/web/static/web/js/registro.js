// Formateo dinámico del RUT con puntos y guion
document.addEventListener("DOMContentLoaded", function () {
  const rutInput = document.getElementById("rut");

  if (rutInput) {
    rutInput.addEventListener("input", () => {
      let raw = rutInput.value.replace(/[^0-9kK]/g, "").toUpperCase();

      if (raw.length > 0) {
        // Máximo 9 caracteres (8 dígitos + dígito verificador)
        raw = raw.slice(0, 9);
        let cuerpo = raw.slice(0, -1);
        let dv = raw.slice(-1);
        // Agregar puntos cada 3 dígitos desde el final
        let formatted = "";
        for (let i = cuerpo.length; i > 0; i -= 3) {
          let start = Math.max(i - 3, 0);
          let section = cuerpo.slice(start, i);
          formatted = section + (formatted ? "." + formatted : "");
        }
        rutInput.value = formatted + "-" + dv;
      }
    });
  }

  // Validación real de RUT (módulo 11)
  const form = document.querySelector("form");
  if (form) {
    form.addEventListener("submit", function (e) {
      if (!validarRut(rutInput.value)) {
        e.preventDefault();
        alert("El RUT ingresado no es válido.");
      }
    });
  }

  function validarRut(rutCompleto) {
    rutCompleto = rutCompleto.replace(/\./g, "").replace("-", "");
    const cuerpo = rutCompleto.slice(0, -1);
    let dv = rutCompleto.slice(-1).toUpperCase();

    let suma = 0;
    let multiplo = 2;

    for (let i = cuerpo.length - 1; i >= 0; i--) {
      suma += parseInt(cuerpo.charAt(i)) * multiplo;
      multiplo = multiplo < 7 ? multiplo + 1 : 2;
    }

    const dvEsperado = 11 - (suma % 11);
    let dvFinal = dvEsperado === 11 ? "0" : dvEsperado === 10 ? "K" : dvEsperado.toString();
    return dv === dvFinal;
  }

  // Teléfono: permitir solo 9 dígitos y formato 9 8765 4321
  const telInput = document.getElementById("telefono");
  if (telInput) {
    telInput.addEventListener("input", () => {
      let num = telInput.value.replace(/\D/g, "").slice(0, 9);
      let formatted = "";
      if (num.length > 0) formatted = num.slice(0, 1);
      if (num.length > 1) formatted += " " + num.slice(1, 5);
      if (num.length > 5) formatted += " " + num.slice(5, 9);
      telInput.value = formatted;
    });
  }

  function validarCoincidenciaContrasena() {
  const pass1 = document.querySelector('input[name="contrasenia"]').value;
  const pass2 = document.querySelector('input[name="confirmar_contrasenia"]');
  if (pass1 && pass1 !== pass2.value) {
    pass2.setCustomValidity("Las contraseñas no coinciden");
  } else {
    pass2.setCustomValidity("");
  }
}

});
