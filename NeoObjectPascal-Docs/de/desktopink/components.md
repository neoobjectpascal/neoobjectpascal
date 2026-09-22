# DesktopInk-Komponenten

DesktopInk bietet **26 Komponenten**, nach Kategorien geordnet. Alle erhalten Props als [Record-Literal](../language/variables-and-types) `#{ ... }`.

## Layout

| Komponent   | Eigenschaften                              | Beschreibung                        |
|-------------|-------------------------------------------|-------------------------------------|
| `Window`    | `padding`, `gap`, `title`                | Bildschirmwurzel (eine pro Screen)  |
| `Container` | `padding`, `gap`, `width`                | Zentriert und begrenzt die Breite   |
| `Section`   | `padding`, `gap`, `title`                | Gruppierung mit optionalem Titel    |
| `Grid`      | `cols`, `gap`                            | Gleichspaltiges Raster              |
| `Row`       | `gap`                                    | Horizontale Hauptachse              |
| `Card`      | `padding`, `gap`, `width`, `height`      | Erhabene Oberfläche mit Rand        |
| `Sidebar`   | `width`, `gap`                           | Fixe Seitenleiste links             |

## Formulare

| Komponent      | Eigenschaften                                                         |
|----------------|-----------------------------------------------------------------------|
| `Button`       | `text`, `variant` (`primary`, `secondary`), `disabled`, `onClick`    |
| `TextInput`    | `placeholder`, `value`, `onChange`, `autoFocus`, `width`              |
| `PasswordInput`| `placeholder`, `value`, `onChange`, `width`                           |
| `TextArea`     | `placeholder`, `value`, `onChange`, `rows`, `width`                   |
| `Select`       | `options`, `value`, `onChange`, `width`                               |
| `Checkbox`     | `checked`, `onChange`, Positionaltext                                |
| `Form`         | `gap`                                                                 |

## Daten

| Komponent | Eigenschaften                                                                              |
|-----------|--------------------------------------------------------------------------------------------|
| `Table`   | `columns`, `rows`, `editable`, `editors`, `onChange`, `onSave`, `selectedRow`, `onRowClick` |
| `List`    | `items`                                                                                     |
| `Chart`   | `type`, `data`                                                                              |

## Modal

| Eigenschaft       | Typ     | Standard | Beschreibung                            |
|-------------------|---------|----------|-----------------------------------------|
| `open`            | Boolean | `false`  | Steuert die Sichtbarkeit                |
| `title`           | String  | —        | Modaltitel                              |
| `width`           | Integer | Intrinsisch | Panelbreite                          |
| `height`          | Integer | Intrinsisch | Panelhöhe                           |
| `onClose`         | Callback| —        | Wird beim Schließen aufgerufen          |
| `closeOnEscape`   | Boolean | `true`   | Schließen mit Esc                       |
| `closeOnBackdrop` | Boolean | `true`   | Schließen durch Klick außen             |

Siehe die vollständige englische Dokumentation für detaillierte Beispiele.