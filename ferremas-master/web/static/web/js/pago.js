document.addEventListener('DOMContentLoaded', () => {
  const metodoPago = document.getElementById('metodoPago');
  const paypalContainer = document.getElementById('paypal-button-container');
  const infoPago = document.getElementById('infoPago');
  const formTransferencia = document.getElementById('form-transferencia');
  const formTarjeta = document.getElementById('form-tarjeta');
  const formDebito = document.getElementById('form-debito');
  const formPago = document.getElementById('form-pago');

  // Render PayPal Buttons 
  paypal.Buttons({
    createOrder: (data, actions) => actions.order.create({
      purchase_units: [{ amount: { value: '9999' } }]
    }),
    onApprove: () => alert('Pago simulado completado!')
  }).render('#paypal-button-container');

  function ocultarFormularios() {
    formTransferencia.style.display = 'none';
    formTarjeta.style.display = 'none';
    formDebito.style.display = 'none';
    paypalContainer.style.display = 'none';
  }

  function actualizarPago() {
    ocultarFormularios();

    switch (metodoPago.value) {
      case 'paypal':
        paypalContainer.style.display = 'block';
        if (infoPago) infoPago.textContent = '';
        break;
      case 'tarjeta':
        formTarjeta.style.display = 'block';
        if (infoPago) infoPago.textContent = 'Simulación de pago con tarjeta de crédito seleccionada.';
        break;
      case 'debito':
        formDebito.style.display = 'block';
        if (infoPago) infoPago.textContent = 'Simulación de pago con débito / RedCompra seleccionada.';
        break;
      case 'transferencia':
        formTransferencia.style.display = 'block';
        if (infoPago) infoPago.textContent = 'Simulación de pago por transferencia seleccionada.';
        break;
      default:
        if (infoPago) infoPago.textContent = '';
        break;
    }
  }

  metodoPago.addEventListener('change', actualizarPago);

  formPago.addEventListener('submit', (e) => {
    e.preventDefault();

    let valido = true;
    let msgError = '';
    const metodo = metodoPago.value;

    if (metodo === 'transferencia') {
      const banco = formTransferencia.querySelector('input[name="banco"]').value.trim();
      const num = formTransferencia.querySelector('input[name="numero_transferencia"]').value.trim();
      if (!banco || !num) {
        valido = false;
        msgError = 'Completa los datos de transferencia.';
      }
    } else if (metodo === 'tarjeta') {
      const numTarj = formTarjeta.querySelector('input[name="numero_tarjeta"]').value.trim();
      const exp = formTarjeta.querySelector('input[name="expiracion"]').value.trim();
      const cvv = formTarjeta.querySelector('input[name="cvv"]').value.trim();
      if (!numTarj || !exp || !cvv) {
        valido = false;
        msgError = 'Completa los datos de tarjeta.';
      }
    } else if (metodo === 'debito') {
      const bancoDeb = formDebito.querySelector('input[name="banco_debito"]').value.trim();
      const numDeb = formDebito.querySelector('input[name="numero_debito"]').value.trim();
      if (!bancoDeb || !numDeb) {
        valido = false;
        msgError = 'Completa los datos de débito.';
      }
    } else if (metodo === 'paypal') {

    } else {
      valido = false;
      msgError = 'Selecciona un método de pago válido.';
    }

    if (!valido) {
      alert(msgError);
      return;
    }

    alert('✅ ¡Pago realizado con éxito!');
    formPago.reset();
    metodoPago.value = 'tarjeta'; 
    actualizarPago();

    setTimeout(() => {
      window.location.href = "/catalogo/";
    }, 1500);
  });


  actualizarPago();
});
