## BearKicks Domain Validation & Checkout

### Value Objects
Perfil: FirstName, LastName, PhoneNumber, Address, BirthDate, Username, Password.
Pago: CardNumber (Luhn + brand + last4), ExpiryDate (MM/YY futuro), Cvv (3-4 dígitos), CardHolderName (solo letras y espacios, longitud segura).

Cada VO expone `create(raw): Result<VO>` devolviendo error de dominio ya localizado. Nunca se accede directamente a datos inseguros (ej: número completo tarjeta) fuera del VO.

### Flujo de Checkout
1. Carrito muestra botón "Pagar" que abre bottom sheet.
2. Tab Tarjeta valida campos con VO en tiempo real.
3. Tab QR genera payload determinístico `provider:total:timestamp` y muestra previsualización (placeholder). El hash SHA-256 se persiste.
4. Use cases `CheckoutWithCardUseCase` y `CheckoutWithQrUseCase` construyen `PaymentInfo` y guardan `OrderPaymentEntity`.

### Persistencia (Room v4)
Tablas: favorites, cart, orders, order_items, order_payment.
`order_payment` (orderId PK) almacena método (CARD / QR), brand, last4, provider y payloadHash (SHA-256 para integridad).

### Historial
`OrdersHistoryScreen` muestra fecha, total, mini-strip de items y método (••••last4 o hash abreviado). Botón eliminar aplica borrado en cascada.

### Eliminación
`DeleteOrderUseCase` elimina pedido + items + pago. `ClearOrdersUseCase` elimina todos los pedidos del usuario.

### Seguridad y Privacidad
Solo se guarda brand + last4; no se guarda CVV, número completo ni fecha original. QR usa hash fuerte para evitar colisiones triviales y permitir verificación.

### Próximos Pasos
- `OrderDetailScreen` (pendiente) para ver cada item y pago.
- Reemplazar preview de texto QR por imagen con ZXing (`generateQrBitmap`).
- Tests unitarios VO y checkout.

### Tests Sugeridos
VO: casos válidos, formato inválido, límites longitud, expiración pasada.
Checkout: carrito vacío, errores VO, éxito (verificar inserción pago y hash SHA-256 formato hex 64 chars).

### Convenciones
- Errores en español con mensajes cortos y acción implícita.
- `Result` para flujos de validación rápida sin excepciones lanzadas.

### Gestión de Secretos / API Keys
Este repositorio NO incluye credenciales reales de Firebase.

1. Archivo real: `app/google-services.json` (IGNORADO por `.gitignore`).
2. Archivo de ejemplo: `app/google-services.sample.json` con placeholders (`REPLACE_ME`).
3. Antes de compilar, la tarea Gradle `verifyGoogleServices` falla si el real no existe.

Pasos para configurar entorno local:
```
cp app/google-services.sample.json app/google-services.json
# Edita el campo current_key y demás valores reales del proyecto Firebase
```

Repositorio reinicializado (historial git limpiado el YYYY-MM-DD tras incidencia CI).

## CI Bitrise

Se usa `bitrise.yml` en la raíz con workflow `primary` que:

1. Clona el repo y restaura caché.
2. Instala componentes Android mínimos.
3. (Opcional) Restaura `app/google-services.json` desde secreto Base64 `GOOGLE_SERVICES_JSON_B64`.
4. Ejecuta tests de unidad: `./gradlew testDebugUnitTest`.
5. Ensambla `app:release` para generar AAB.
6. Empuja caché y despliega artefactos.

### google-services.json

El archivo real está ignorado por `.gitignore`. Para evitar fallo en CI:

- Se añadió lógica en `app/build.gradle.kts` que salta verificación si `CI=true` o `GOOGLE_SERVICES_SKIP_VERIFY=true`.
- Para pruebas puras de lógica, no es necesario el archivo.
- Si se requiere Firebase real, crea un secreto en Bitrise `GOOGLE_SERVICES_JSON_B64`:
	```bash
	base64 -w0 app/google-services.json > google-services.json.b64
	# Copiar contenido al secreto
	```

Si NO configuras el secreto, el workflow genera un stub `google-services.json` con claves ficticias solo para permitir que el plugin compile y ejecutar tests de dominio/UI sin llamadas reales.

### Comandos locales

```powershell
cd c:\Users\santi\StudioProjects\BearKicks
./gradlew testDebugUnitTest
./gradlew testDebugUnitTest -PdisableFirebase   # Ejecuta tests sin aplicar el plugin Google Services
./gradlew assembleRelease
```

### Próximos pasos

- Añadir remoto nuevo: `git remote add origin <URL>` y hacer primer push.
- Bitrise: usar workflow `run_tests` para sólo tests (usa `-PdisableFirebase`).
- Para build completa + AAB usar workflow `primary` (requiere secreto Base64 o se generará stub). 


CI/CD (opcional):
- Descarga el `google-services.json` desde un secret manager y colócalo en `app/` antes de ejecutar Gradle.
- Ejemplo (Linux): `echo "$FIREBASE_JSON" > app/google-services.json`.

Buenas prácticas:
- Nunca subas el archivo real.
- Mantén las claves en un gestor de secretos (GitHub Actions Secrets, etc.).
- Para otras claves (p.e. REST APIs), usa `local.properties` o un plugin de secretos y variables de entorno.

