# DesktopInk — introduction

**DesktopInk** est le framework d'applications de bureau natives de NeoObjectPascal. Avec `uses desktopink;` vous construisez des fenêtres natives avec des composants visuels déclaratifs, un thème clair/sombre, et le même modèle réactif que les autres runtimes.

Contrairement à TerminalInk (terminal) et WebInk (serveur web), DesktopInk ouvre une **véritable fenêtre Swing/Java2D** — sans dépendances externes. Chaque composant est dessiné par le framework avec des coins arrondis, la typographie système et un aspect professionnel inspiré de [shadcn/ui](https://ui.shadcn.com).

```npas
uses desktopink;

begin
    render(#{ principal: principal }, #{
        title: "Mon App",
        width: 800,
        height: 600,
        centered: true,
        theme: "auto"
    });
end.
```

## Options de rendu

| Option     | Type    | Défaut   | Description                            |
|------------|---------|----------|----------------------------------------|
| `title`    | String  | App name | Titre de la fenêtre                    |
| `width`    | Integer | 960      | Largeur initiale                       |
| `height`   | Integer | 720      | Hauteur initiale                       |
| `centered` | Boolean | `true`   | Centrer la fenêtre                     |
| `maximized`| Boolean | `false`  | Ouvrir la fenêtre maximisée            |
| `theme`    | String  | `"auto"` | `"light"`, `"dark"` ou `"auto"` (système) |

## Thème

DesktopInk dispose de deux thèmes complets — clair et sombre — plus le mode `auto`, qui suit la préférence du système. Changez de thème avec :

```npas
setTheme("dark");
setTheme("light");
setTheme("auto");
```

Voir la [liste complète des composants](./components) en anglais pour les détails.