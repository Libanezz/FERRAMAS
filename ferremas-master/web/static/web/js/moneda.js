document.addEventListener("DOMContentLoaded", () => {
  const selector = document.getElementById("selector-moneda");
  const precios = document.querySelectorAll(".price");
  let tasas = { CLP: 1 };

  // Cargar tasas desde la API
  fetch("https://mindicador.cl/api")
    .then(resp => resp.json())
    .then(data => {
      tasas.USD = data.dolar.valor; // 1 USD = 930 CLP (ejemplo)
      tasas.EUR = data.euro.valor;
      aplicarCambio(selector.value);
    })
    .catch(err => {
      console.error("Error al obtener tasas:", err);
      // Tasas de respaldo si falla la API
      tasas.USD = 930;
      tasas.EUR = 980;
      aplicarCambio(selector.value);
    });

  selector.addEventListener("change", () => aplicarCambio(selector.value));

  function aplicarCambio(moneda) {
    precios.forEach(el => {
      const base = parseFloat(el.dataset.base);
      if (!isNaN(base)) {
        let conv = base;
        if (moneda !== "CLP") {
          conv = base / tasas[moneda];
        }
        el.textContent = `${conv.toFixed(2)} ${moneda}`;
      }
    });
  }
});
