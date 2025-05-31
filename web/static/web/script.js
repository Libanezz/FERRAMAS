/*fetch('/api/')
  .then(response => response.json())
  .then(data => {
    // Manipulas el DOM con los datos
  });*/


document.addEventListener('DOMContentLoaded', () => {
  fetch('/api/productos/')
    .then(response => response.json())
    .then(data => {
      const ul = document.getElementById('lista-productos');
      ul.innerHTML = '';  // Limpiar contenido si había algo
      data.forEach(producto => {
        const li = document.createElement('li');
        li.textContent = `${producto.nombre} - $${producto.precio}`;
        ul.appendChild(li);
      });
    })
    .catch(error => console.error('Error al cargar productos:', error));
});





  /*fetch('api/')
  .then(response => response.json())
  .then(data => {
    console.log(data); // Aquí puedes usar los datos para renderizar tu frontend
  })
  .catch(error => console.error('Error:', error));*/


  /* fetch('/api/')
    .then(response => response.json())
    .then(data => {
      const lista = document.getElementById('lista-productos');
      lista.innerHTML = ''; // limpia contenido previo

      data.forEach(producto => {
        const li = document.createElement('li');
        li.textContent = `${producto.nombre} - $${producto.precio}`;
        lista.appendChild(li);
      });
    })
    .catch(error => console.error('Error al cargar productos:', error));*/