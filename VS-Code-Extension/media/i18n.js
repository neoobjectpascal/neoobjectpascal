// i18n.js — translation layer for the NeoObjectPascal UI Builder.
// Works both in the webview (attaches to `window.NpI18n`) and in the extension
// host (`require('./media/i18n.js')`). Keys are the canonical Portuguese strings;
// `tr(pt, lang)` returns the translation or falls back to the PT key. The five
// languages match the documentation: pt (default), en, de, fr, it.
(function (root, factory) {
  const mod = factory();
  if (typeof module !== 'undefined' && module.exports) module.exports = mod;
  if (root) root.NpI18n = mod;
})(typeof window !== 'undefined' ? window : (typeof globalThis !== 'undefined' ? globalThis : this), function () {
  const LANGS = ['pt', 'en', 'de', 'fr', 'it'];

  // Only non-Portuguese entries are stored; PT is the key itself.
  const DICT = {
    // ── toolbar ──────────────────────────────────────────────────────────────
    'Nova tela': { en: 'New screen', de: 'Neuer Bildschirm', fr: 'Nouvel écran', it: 'Nuova schermata' },
    'Nova rota/tela': { en: 'New route/screen', de: 'Neue Route/Bildschirm', fr: 'Nouvelle route/écran', it: 'Nuova rotta/schermata' },
    'sincroniza com o .npas ao salvar': { en: 'syncs to the .npas on save', de: 'synchronisiert beim Speichern mit der .npas', fr: 'synchronise avec le .npas à l’enregistrement', it: 'sincronizza con il .npas al salvataggio' },
    'Desfazer': { en: 'Undo', de: 'Rückgängig', fr: 'Annuler', it: 'Annulla' },
    'Refazer': { en: 'Redo', de: 'Wiederholen', fr: 'Rétablir', it: 'Ripeti' },
    'Rodar ao vivo': { en: 'Run live', de: 'Live ausführen', fr: 'Exécuter en direct', it: 'Esegui dal vivo' },
    'Idioma': { en: 'Language', de: 'Sprache', fr: 'Langue', it: 'Lingua' },

    // ── palette ──────────────────────────────────────────────────────────────
    'Buscar componente…': { en: 'Search component…', de: 'Komponente suchen…', fr: 'Rechercher un composant…', it: 'Cerca componente…' },
    'Tipografia': { en: 'Typography', de: 'Typografie', fr: 'Typographie', it: 'Tipografia' },
    'Navegação': { en: 'Navigation', de: 'Navigation', fr: 'Navigation', it: 'Navigazione' },
    'Formulários': { en: 'Forms', de: 'Formulare', fr: 'Formulaires', it: 'Moduli' },
    'Dados': { en: 'Data', de: 'Daten', fr: 'Données', it: 'Dati' },
    'Texto': { en: 'Text', de: 'Text', fr: 'Texte', it: 'Testo' },
    'Entrada': { en: 'Input', de: 'Eingabe', fr: 'Saisie', it: 'Input' },
    'Listas': { en: 'Lists', de: 'Listen', fr: 'Listes', it: 'Elenchi' },

    // ── canvas ───────────────────────────────────────────────────────────────
    'Remover': { en: 'Remove', de: 'Entfernen', fr: 'Supprimer', it: 'Rimuovi' },
    'Arraste um componente da paleta para dentro do {name} para começar.': {
      en: 'Drag a component from the palette into {name} to begin.',
      de: 'Ziehen Sie eine Komponente aus der Palette in {name}, um zu beginnen.',
      fr: 'Faites glisser un composant de la palette dans {name} pour commencer.',
      it: 'Trascina un componente dalla palette dentro {name} per iniziare.' },
    'Canvas — pré-visualização real · arraste da paleta para inserir · clique para selecionar': {
      en: 'Canvas — live preview · drag from the palette to insert · click to select',
      de: 'Canvas — Live-Vorschau · aus der Palette ziehen zum Einfügen · klicken zum Auswählen',
      fr: 'Canvas — aperçu en direct · glisser depuis la palette pour insérer · cliquer pour sélectionner',
      it: 'Canvas — anteprima dal vivo · trascina dalla palette per inserire · clicca per selezionare' },

    // ── inspector tabs / props ───────────────────────────────────────────────
    'Propriedades': { en: 'Properties', de: 'Eigenschaften', fr: 'Propriétés', it: 'Proprietà' },
    'Estado': { en: 'State', de: 'Zustand', fr: 'État', it: 'Stato' },
    'Eventos': { en: 'Events', de: 'Ereignisse', fr: 'Événements', it: 'Eventi' },
    'Componente': { en: 'Component', de: 'Komponente', fr: 'Composant', it: 'Componente' },
    'Selecione um componente no canvas para editar suas propriedades.': {
      en: 'Select a component on the canvas to edit its properties.',
      de: 'Wählen Sie eine Komponente auf dem Canvas, um ihre Eigenschaften zu bearbeiten.',
      fr: 'Sélectionnez un composant sur le canvas pour modifier ses propriétés.',
      it: 'Seleziona un componente sul canvas per modificarne le proprietà.' },
    'evento': { en: 'event', de: 'Ereignis', fr: 'événement', it: 'evento' },

    // ── visible + fx ─────────────────────────────────────────────────────────
    'Visível': { en: 'Visible', de: 'Sichtbar', fr: 'Visible', it: 'Visibile' },
    'Sempre': { en: 'Always', de: 'Immer', fr: 'Toujours', it: 'Sempre' },
    'Condição (fx)': { en: 'Condition (fx)', de: 'Bedingung (fx)', fr: 'Condition (fx)', it: 'Condizione (fx)' },
    'Voltar a valor fixo': { en: 'Back to fixed value', de: 'Zurück zum festen Wert', fr: 'Revenir à une valeur fixe', it: 'Torna al valore fisso' },
    'Vincular a uma variável/expressão': { en: 'Bind to a variable/expression', de: 'An Variable/Ausdruck binden', fr: 'Lier à une variable/expression', it: 'Collega a una variabile/espressione' },
    'variável ou expressão (ex.: itens, dados)': { en: 'variable or expression (e.g.: items, data)', de: 'Variable oder Ausdruck (z. B.: items, data)', fr: 'variable ou expression (ex. : items, data)', it: 'variabile o espressione (es.: items, data)' },
    'Gráfico': { en: 'Chart', de: 'Diagramm', fr: 'Graphique', it: 'Grafico' },

    // ── chart editor ─────────────────────────────────────────────────────────
    'Tipo de gráfico': { en: 'Chart type', de: 'Diagrammtyp', fr: 'Type de graphique', it: 'Tipo di grafico' },
    'Rótulos (vírgula)': { en: 'Labels (comma)', de: 'Beschriftungen (Komma)', fr: 'Étiquettes (virgule)', it: 'Etichette (virgola)' },
    'Nome da série': { en: 'Series name', de: 'Name der Reihe', fr: 'Nom de la série', it: 'Nome della serie' },
    'Valores (vírgula)': { en: 'Values (comma)', de: 'Werte (Komma)', fr: 'Valeurs (virgule)', it: 'Valori (virgola)' },
    'sem borda': { en: 'no border', de: 'kein Rahmen', fr: 'sans bordure', it: 'nessun bordo' },

    // ── event editor ─────────────────────────────────────────────────────────
    '(nenhuma ação)': { en: '(no action)', de: '(keine Aktion)', fr: '(aucune action)', it: '(nessuna azione)' },
    'Incrementar variável': { en: 'Increment variable', de: 'Variable erhöhen', fr: 'Incrémenter une variable', it: 'Incrementa variabile' },
    'Definir variável': { en: 'Set variable', de: 'Variable setzen', fr: 'Définir une variable', it: 'Imposta variabile' },
    'Usar valor do input': { en: 'Use input value', de: 'Eingabewert verwenden', fr: 'Utiliser la valeur du champ', it: 'Usa il valore dell’input' },
    'Ir para rota': { en: 'Go to route', de: 'Zur Route gehen', fr: 'Aller à la route', it: 'Vai alla rotta' },
    'Focar componente': { en: 'Focus component', de: 'Komponente fokussieren', fr: 'Focaliser le composant', it: 'Focalizza componente' },
    'ação': { en: 'action', de: 'Aktion', fr: 'action', it: 'azione' },
    'Sem código': { en: 'No code', de: 'Ohne Code', fr: 'Sans code', it: 'Senza codice' },
    'Código': { en: 'Code', de: 'Code', fr: 'Code', it: 'Codice' },
    'valor (ex.: 10 ou "texto")': { en: 'value (e.g.: 10 or "text")', de: 'Wert (z. B.: 10 oder "Text")', fr: 'valeur (ex. : 10 ou "texte")', it: 'valore (es.: 10 o "testo")' },
    '(sem variáveis)': { en: '(no variables)', de: '(keine Variablen)', fr: '(aucune variable)', it: '(nessuna variabile)' },
    '(defina "Chave" nos componentes)': { en: '(set "Key" on components)', de: '(setzen Sie "Schlüssel" an Komponenten)', fr: '(définissez « Clé » sur les composants)', it: '(imposta "Chiave" nei componenti)' },

    // ── state / events tabs ──────────────────────────────────────────────────
    'variáveis globais': { en: 'global variables', de: 'globale Variablen', fr: 'variables globales', it: 'variabili globali' },
    'Compartilhadas entre telas e eventos. Os eventos leem e alteram estas variáveis.': {
      en: 'Shared across screens and events. Events read and change these variables.',
      de: 'Gemeinsam für Bildschirme und Ereignisse. Ereignisse lesen und ändern diese Variablen.',
      fr: 'Partagées entre les écrans et les événements. Les événements lisent et modifient ces variables.',
      it: 'Condivise tra schermate ed eventi. Gli eventi leggono e modificano queste variabili.' },
    'inicial': { en: 'initial', de: 'Startwert', fr: 'initiale', it: 'iniziale' },
    'Nova variável': { en: 'New variable', de: 'Neue Variable', fr: 'Nouvelle variable', it: 'Nuova variabile' },
    'handlers': { en: 'handlers', de: 'Handler', fr: 'gestionnaires', it: 'gestori' },
    'Nenhum evento ainda. Selecione um componente (ex.: Button) e configure o <b>onClick</b> na aba Propriedades.': {
      en: 'No events yet. Select a component (e.g.: Button) and configure <b>onClick</b> in the Properties tab.',
      de: 'Noch keine Ereignisse. Wählen Sie eine Komponente (z. B.: Button) und konfigurieren Sie <b>onClick</b> im Tab Eigenschaften.',
      fr: 'Aucun événement pour l’instant. Sélectionnez un composant (ex. : Button) et configurez <b>onClick</b> dans l’onglet Propriétés.',
      it: 'Ancora nessun evento. Seleziona un componente (es.: Button) e configura <b>onClick</b> nella scheda Proprietà.' },

    // ── widget field labels ──────────────────────────────────────────────────
    'Colunas': { en: 'Columns', de: 'Spalten', fr: 'Colonnes', it: 'Colonne' },
    'Nível': { en: 'Level', de: 'Ebene', fr: 'Niveau', it: 'Livello' },
    'Cor': { en: 'Color', de: 'Farbe', fr: 'Couleur', it: 'Colore' },
    'Rótulo': { en: 'Label', de: 'Beschriftung', fr: 'Étiquette', it: 'Etichetta' },
    'Valor': { en: 'Value', de: 'Wert', fr: 'Valeur', it: 'Valore' },
    'Delta (opcional)': { en: 'Delta (optional)', de: 'Delta (optional)', fr: 'Delta (facultatif)', it: 'Delta (opzionale)' },
    'Rota (href)': { en: 'Route (href)', de: 'Route (href)', fr: 'Route (href)', it: 'Rotta (href)' },
    'Variante': { en: 'Variant', de: 'Variante', fr: 'Variante', it: 'Variante' },
    'Placeholder': { en: 'Placeholder', de: 'Platzhalter', fr: 'Placeholder', it: 'Placeholder' },
    'Linhas': { en: 'Rows', de: 'Zeilen', fr: 'Lignes', it: 'Righe' },
    'Opções (separadas por vírgula)': { en: 'Options (comma-separated)', de: 'Optionen (durch Komma getrennt)', fr: 'Options (séparées par des virgules)', it: 'Opzioni (separate da virgola)' },
    'Marcado': { en: 'Checked', de: 'Aktiviert', fr: 'Coché', it: 'Selezionato' },
    'Colunas (vírgula)': { en: 'Columns (comma)', de: 'Spalten (Komma)', fr: 'Colonnes (virgule)', it: 'Colonne (virgola)' },
    'Linhas (uma por linha; células por vírgula)': { en: 'Rows (one per line; cells by comma)', de: 'Zeilen (eine pro Zeile; Zellen durch Komma)', fr: 'Lignes (une par ligne ; cellules par virgule)', it: 'Righe (una per riga; celle per virgola)' },
    'Itens (vírgula)': { en: 'Items (comma)', de: 'Elemente (Komma)', fr: 'Éléments (virgule)', it: 'Elementi (virgola)' },
    'Numerada': { en: 'Numbered', de: 'Nummeriert', fr: 'Numérotée', it: 'Numerata' },
    'Valor (0–100)': { en: 'Value (0–100)', de: 'Wert (0–100)', fr: 'Valeur (0–100)', it: 'Valore (0–100)' },
    'Borda': { en: 'Border', de: 'Rahmen', fr: 'Bordure', it: 'Bordo' },
    'Cor da borda': { en: 'Border color', de: 'Rahmenfarbe', fr: 'Couleur de bordure', it: 'Colore del bordo' },
    'Espaço entre filhos (gap)': { en: 'Gap between children', de: 'Abstand zwischen Kindern (gap)', fr: 'Espace entre enfants (gap)', it: 'Spazio tra figli (gap)' },
    'Alinhar (eixo cruzado)': { en: 'Align (cross axis)', de: 'Ausrichten (Querachse)', fr: 'Aligner (axe transversal)', it: 'Allinea (asse trasversale)' },
    'Distribuir (eixo principal)': { en: 'Distribute (main axis)', de: 'Verteilen (Hauptachse)', fr: 'Distribuer (axe principal)', it: 'Distribuisci (asse principale)' },
    'Direção': { en: 'Direction', de: 'Richtung', fr: 'Direction', it: 'Direzione' },
    'Valor inicial': { en: 'Initial value', de: 'Anfangswert', fr: 'Valeur initiale', it: 'Valore iniziale' },
    'Chave (para foco / estado)': { en: 'Key (for focus / state)', de: 'Schlüssel (für Fokus / Zustand)', fr: 'Clé (pour le focus / l’état)', it: 'Chiave (per focus / stato)' },
    'Foco inicial (autoFocus)': { en: 'Initial focus (autoFocus)', de: 'Anfangsfokus (autoFocus)', fr: 'Focus initial (autoFocus)', it: 'Focus iniziale (autoFocus)' },
    'Negrito': { en: 'Bold', de: 'Fett', fr: 'Gras', it: 'Grassetto' },
    'Esmaecido (dim)': { en: 'Dimmed (dim)', de: 'Gedimmt (dim)', fr: 'Atténué (dim)', it: 'Attenuato (dim)' },
    'Invertido': { en: 'Inverted', de: 'Invertiert', fr: 'Inversé', it: 'Invertito' },
    'Domínios (vírgula)': { en: 'Domains (comma)', de: 'Domänen (Komma)', fr: 'Domaines (virgule)', it: 'Domini (virgola)' },
    'Padrão': { en: 'Default', de: 'Standard', fr: 'Par défaut', it: 'Predefinito' },
    'Enter confirma': { en: 'Enter confirms', de: 'Enter bestätigt', fr: 'Entrée confirme', it: 'Invio conferma' },
    'Opções (uma por linha; "rótulo = valor")': { en: 'Options (one per line; "label = value")', de: 'Optionen (eine pro Zeile; "Beschriftung = Wert")', fr: 'Options (une par ligne ; « étiquette = valeur »)', it: 'Opzioni (una per riga; "etichetta = valore")' },
    'Linhas visíveis': { en: 'Visible rows', de: 'Sichtbare Zeilen', fr: 'Lignes visibles', it: 'Righe visibili' },
    'Título': { en: 'Title', de: 'Titel', fr: 'Titre', it: 'Titolo' },
    'Mensagem': { en: 'Message', de: 'Nachricht', fr: 'Message', it: 'Messaggio' },
    'Marcador': { en: 'Marker', de: 'Markierung', fr: 'Marqueur', it: 'Marcatore' },
    'Sufixo do número': { en: 'Number suffix', de: 'Zahlensuffix', fr: 'Suffixe du numéro', it: 'Suffisso del numero' },

    // ── extension host: templates, prompts, warnings ─────────────────────────
    'WebInk — em branco': { en: 'WebInk — blank', de: 'WebInk — leer', fr: 'WebInk — vierge', it: 'WebInk — vuoto' },
    'Página web vazia': { en: 'Empty web page', de: 'Leere Webseite', fr: 'Page web vide', it: 'Pagina web vuota' },
    'Navbar, StatCards, gráfico e contador': { en: 'Navbar, StatCards, chart and counter', de: 'Navbar, StatCards, Diagramm und Zähler', fr: 'Navbar, StatCards, graphique et compteur', it: 'Navbar, StatCards, grafico e contatore' },
    'TerminalInk — em branco': { en: 'TerminalInk — blank', de: 'TerminalInk — leer', fr: 'TerminalInk — vierge', it: 'TerminalInk — vuoto' },
    'Tela de terminal vazia': { en: 'Empty terminal screen', de: 'Leerer Terminal-Bildschirm', fr: 'Écran de terminal vide', it: 'Schermata di terminale vuota' },
    'TerminalInk — Formulário': { en: 'TerminalInk — Form', de: 'TerminalInk — Formular', fr: 'TerminalInk — Formulaire', it: 'TerminalInk — Modulo' },
    'Título, campo e badge': { en: 'Title, field and badge', de: 'Titel, Feld und Badge', fr: 'Titre, champ et badge', it: 'Titolo, campo e badge' },
    'Escolha um modelo para o novo arquivo .xnpas': { en: 'Choose a template for the new .xnpas file', de: 'Wählen Sie eine Vorlage für die neue .xnpas-Datei', fr: 'Choisissez un modèle pour le nouveau fichier .xnpas', it: 'Scegli un modello per il nuovo file .xnpas' },
    'Nome do arquivo (.xnpas)': { en: 'File name (.xnpas)', de: 'Dateiname (.xnpas)', fr: 'Nom du fichier (.xnpas)', it: 'Nome del file (.xnpas)' },
    'Use letras, números, _ ou - (começando por letra).': { en: 'Use letters, numbers, _ or - (starting with a letter).', de: 'Verwenden Sie Buchstaben, Zahlen, _ oder - (beginnend mit einem Buchstaben).', fr: 'Utilisez des lettres, des chiffres, _ ou - (en commençant par une lettre).', it: 'Usa lettere, numeri, _ o - (iniziando con una lettera).' },
    'Abra uma pasta no VS Code para criar o arquivo.': { en: 'Open a folder in VS Code to create the file.', de: 'Öffnen Sie einen Ordner in VS Code, um die Datei zu erstellen.', fr: 'Ouvrez un dossier dans VS Code pour créer le fichier.', it: 'Apri una cartella in VS Code per creare il file.' },
    'Erro ao criar .xnpas: ': { en: 'Error creating .xnpas: ', de: 'Fehler beim Erstellen der .xnpas: ', fr: 'Erreur lors de la création du .xnpas : ', it: 'Errore nella creazione del .xnpas: ' },
    'Abrir editor visual': { en: 'Open visual editor', de: 'Visuellen Editor öffnen', fr: 'Ouvrir l’éditeur visuel', it: 'Apri l’editor visivo' },
    '{base}.npas é gerado pelo editor visual ({base}.xnpas) e será sobrescrito ao salvar o .xnpas. Edite a interface pelo editor visual.': {
      en: '{base}.npas is generated by the visual editor ({base}.xnpas) and will be overwritten when the .xnpas is saved. Edit the interface in the visual editor.',
      de: '{base}.npas wird vom visuellen Editor ({base}.xnpas) erzeugt und beim Speichern der .xnpas überschrieben. Bearbeiten Sie die Oberfläche im visuellen Editor.',
      fr: '{base}.npas est généré par l’éditeur visuel ({base}.xnpas) et sera écrasé lors de l’enregistrement du .xnpas. Modifiez l’interface dans l’éditeur visuel.',
      it: '{base}.npas è generato dall’editor visivo ({base}.xnpas) e verrà sovrascritto al salvataggio del .xnpas. Modifica l’interfaccia nell’editor visivo.' },
    'NeoObjectPascal UI: .xnpas inválido — .npas não foi gerado.': { en: 'NeoObjectPascal UI: invalid .xnpas — .npas was not generated.', de: 'NeoObjectPascal UI: ungültige .xnpas — .npas wurde nicht erzeugt.', fr: 'NeoObjectPascal UI : .xnpas invalide — le .npas n’a pas été généré.', it: 'NeoObjectPascal UI: .xnpas non valido — .npas non generato.' },
    'NeoObjectPascal UI: erro ao gerar .npas — ': { en: 'NeoObjectPascal UI: error generating .npas — ', de: 'NeoObjectPascal UI: Fehler beim Erzeugen der .npas — ', fr: 'NeoObjectPascal UI : erreur lors de la génération du .npas — ', it: 'NeoObjectPascal UI: errore nella generazione del .npas — ' },
    'Não foi possível rodar: ': { en: 'Could not run: ', de: 'Konnte nicht ausführen: ', fr: 'Impossible d’exécuter : ', it: 'Impossibile eseguire: ' },
  };

  function normalize(l) {
    l = String(l || '').slice(0, 2).toLowerCase();
    return LANGS.indexOf(l) >= 0 ? l : 'pt';
  }

  return {
    langs: LANGS,
    normalize: normalize,
    // Translate a Portuguese key into `lang`, falling back to the key itself.
    tr: function (s, lang) {
      const L = normalize(lang);
      if (L === 'pt') return s;
      const e = DICT[s];
      return (e && e[L]) || s;
    },
  };
});
