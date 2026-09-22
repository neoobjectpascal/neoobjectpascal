# Composants DesktopInk

DesktopInk propose **26 composants**, organisés par catégorie. Tous reçoivent leurs props comme un [littéral d'enregistrement](../language/variables-and-types) `#{ ... }`.

## Disposition

| Composant   | Propriétés                            | Description                        |
|-------------|---------------------------------------|------------------------------------|
| `Window`    | `padding`, `gap`, `title`            | Racine d'écran                    |
| `Container` | `padding`, `gap`, `width`            | Centrer et limiter la largeur      |
| `Grid`      | `cols`, `gap`                        | Grille à colonnes égales           |
| `Row`       | `gap`                                | Axe principal horizontal           |
| `Card`      | `padding`, `gap`, `width`, `height`  | Surface surélevée                  |
| `Sidebar`   | `width`, `gap`                       | Barre latérale fixe                |

## Formulaires

| Composant       | Propriétés                                                         |
|-----------------|--------------------------------------------------------------------|
| `Button`        | `text`, `variant`, `onClick`                                      |
| `TextInput`     | `placeholder`, `value`, `onChange`, `autoFocus`, `width`          |
| `Select`        | `options`, `value`, `onChange`, `width`                           |
| `Checkbox`      | `checked`, `onChange`, texte positionnel                          |

## Données

| Composant | Propriétés                                                                              |
|-----------|------------------------------------------------------------------------------------------|
| `Table`   | `columns`, `rows`, `editable`, `editors`, `onChange`, `onSave`, `selectedRow`, `onRowClick` |

## Modal

| Propriété          | Type    | Défaut   | Description                            |
|--------------------|---------|----------|----------------------------------------|
| `open`             | Boolean | `false`  | Contrôle la visibilité                 |
| `title`            | String  | —        | Titre du modal                         |
| `width`/`height`   | Integer | Intrinsèque | Dimensions du panneau                |
| `onClose`          | Callback| —        | Déclenché à la fermeture              |
| `closeOnEscape`    | Boolean | `true`   | Fermer avec Esc                        |
| `closeOnBackdrop`  | Boolean | `true`   | Fermer en cliquant à l'extérieur       |

Voir la documentation anglaise pour les exemples détaillés.