# TP1 — Conversor de Moneda

## Descripción de la app

Es un conversor entre dólares y euros. Puedes ingresar la cantidad en el campo que corresponda según la opción elegida (convertir a euros o a dólares), ver el tipo de cambio actual (1 $ = X €) y cambiarlo a mano si hace falta. Al tocar **Convertir** aparece el resultado en el otro campo.

## MVVM (cómo lo implementé)

Separé la pantalla de la lógica así:

- **Model:** `CurrencyConverter` tiene el tipo de cambio y hace las cuentas (USD ↔ EUR).
- **ViewModel:** `CurrencyViewModel` guarda el estado que la UI necesita (texto del importe, resultado, mensajes de error, texto del tipo de cambio) y llama al modelo cuando hay que convertir o actualizar la cotización.
- **View:** `MainActivity` solo muestra datos, escucha botones y radios, y le pasa al ViewModel lo que escribe el usuario. No hace la validación “pesada” ni las fórmulas ahí.

Así la UI no mezcla reglas de negocio y si cambia la pantalla, la lógica sigue en el mismo lugar.
