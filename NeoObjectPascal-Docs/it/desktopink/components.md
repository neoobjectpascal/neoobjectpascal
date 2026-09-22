# Componenti DesktopInk

DesktopInk offre **26 componenti**, organizzati per categoria. Tutti ricevono le props come [record literal](../language/variables-and-types) `#{ ... }`.

## Layout

| Componente  | Proprietà                              | Descrizione                          |
|-------------|----------------------------------------|--------------------------------------|
| `Window`    | `padding`, `gap`, `title`              | Radice dello schermo                 |
| `Container` | `padding`, `gap`, `width`              | Centra e limita la larghezza         |
| `Grid`      | `cols`, `gap`                          | Griglia a colonne uguali             |
| `Row`       | `gap`                                  | Asse principale orizzontale          |
| `Card`      | `padding`, `gap`, `width`, `height`    | Superficie rialzata con bordo        |
| `Sidebar`   | `width`, `gap`                         | Barra laterale fissa a sinistra      |

## Formulari

| Componente       | Proprietà                                                         |
|------------------|--------------------------------------------------------------------|
| `Button`         | `text`, `variant`, `onClick`                                      |
| `TextInput`      | `placeholder`, `value`, `onChange`, `autoFocus`, `width`          |
| `Select`         | `options`, `value`, `onChange`, `width`                           |
| `Checkbox`       | `checked`, `onChange`, testo posizionale                          |

## Dati

| Componente | Proprietà                                                                              |
|------------|----------------------------------------------------------------------------------------|
| `Table`    | `columns`, `rows`, `editable`, `editors`, `onChange`, `onSave`, `selectedRow`, `onRowClick` |

## Modal

| Proprietà          | Tipo    | Default  | Descrizione                            |
|--------------------|---------|----------|----------------------------------------|
| `open`             | Boolean | `false`  | Controlla la visibilità                |
| `title`            | String  | —        | Titolo del modal                       |
| `width`/`height`   | Integer | Intrinseco | Dimensioni del pannello              |
| `onClose`          | Callback| —        | Chiamato alla chiusura                |
| `closeOnEscape`    | Boolean | `true`   | Chiudi con Esc                         |
| `closeOnBackdrop`  | Boolean | `true`   | Chiudi cliccando fuori                |

Vedi la documentazione in inglese per gli esempi dettagliati.