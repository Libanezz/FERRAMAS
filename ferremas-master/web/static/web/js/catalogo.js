document.addEventListener('DOMContentLoaded', function () {
  const botones = document.querySelectorAll('.btn-agregar-carrito');

  botones.forEach(btn => {
    btn.addEventListener('click', function () {
      const producto = {
        id: this.dataset.id,
        nombre: this.closest('.card').querySelector('.card-title').textContent,
        marca: this.closest('.card').querySelector('.card-text.text-muted').textContent,
        descripcion: this.closest('.card').querySelectorAll('.card-text')[1].textContent,
        precio: this.closest('.card').querySelector('.price').textContent,
        imagen: this.closest('.card').querySelector('img').src
      };

      let carrito = JSON.parse(localStorage.getItem('carrito')) || [];
      carrito.push(producto);
      localStorage.setItem('carrito', JSON.stringify(carrito));

      const contador = document.getElementById('cart-count');
      if (contador) {
        contador.textContent = carrito.length;
      }

      alert("Producto agregado 🛒");
    });
  });
});