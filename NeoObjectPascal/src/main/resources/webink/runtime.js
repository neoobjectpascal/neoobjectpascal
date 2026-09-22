/* WebInk client runtime — thin server-driven UI client.
 * Renders the current route from the server, wires events (click/change/submit),
 * instantiates Chart.js canvases, and drives URL routing via the History API. */
(function () {
  "use strict";

  var app = document.getElementById("app");
  var charts = new WeakMap(); // canvas -> Chart instance (to destroy on re-render)
  var modalRestoreId = null;

  function post(url, body) {
    return fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    }).then(function (r) { return r.json(); });
  }

  function apply(data) {
    if (data && typeof data.navigate === "string") {
      history.pushState(null, "", data.navigate);
    }
    if (data && typeof data.theme === "string") {
      document.documentElement.setAttribute("data-webink-theme", data.theme);
    }
    // Preserve focus + caret across the full re-render, so a reactive text input
    // (onChange per keystroke) keeps focus and cursor position while you type.
    var ae = document.activeElement;
    var wasInModal = ae && ae.closest && ae.closest("[data-webink-modal]");
    var focusId = ae && ae.getAttribute
      ? ae.getAttribute("data-webink-change") || ae.getAttribute("data-webink-submit") || ae.getAttribute("data-webink-click")
      : null;
    if (!wasInModal && focusId) modalRestoreId = focusId;
    var caret = ae && typeof ae.selectionStart === "number" ? ae.selectionStart : null;

    app.innerHTML = (data && data.html) || "";
    wire();

    if (focusId) {
      var el = app.querySelector(
        '[data-webink-change="' + focusId + '"],[data-webink-submit="' + focusId + '"]');
      if (el) {
        el.focus();
        if (caret != null && el.setSelectionRange) {
          try { el.setSelectionRange(caret, caret); } catch (e) {}
        }
      }
    }
    var modal = app.querySelector("[data-webink-modal-dialog]");
    if (modal) {
      if (!app.contains(document.activeElement)) modal.focus();
    } else if (modalRestoreId) {
      var restore = app.querySelector('[data-webink-click="' + modalRestoreId + '"],[data-webink-change="' + modalRestoreId + '"],[data-webink-submit="' + modalRestoreId + '"]');
      if (restore) restore.focus();
      modalRestoreId = null;
    }
  }

  // Render a route fresh (used for links / popstate / first load — no callback).
  function renderRoute(route) {
    return post("/webink/render", { route: route }).then(apply);
  }

  // Fire a callback event; the server returns the re-rendered HTML (+ optional navigate).
  function fireEvent(handlerId, value) {
    return post("/webink/event", {
      route: location.pathname,
      handlerId: handlerId,
      value: value === undefined ? null : value,
    }).then(apply);
  }

  function wire() {
    // navigation links
    app.querySelectorAll("[data-webink-nav]").forEach(function (el) {
      el.addEventListener("click", function (e) {
        e.preventDefault();
        var to = el.getAttribute("data-webink-nav");
        history.pushState(null, "", to);
        renderRoute(to);
      });
    });

    // clickables (buttons, table rows)
    app.querySelectorAll("[data-webink-click]").forEach(function (el) {
      el.addEventListener("click", function () {
        var rowIndex = el.getAttribute("data-webink-row");
        fireEvent(el.getAttribute("data-webink-click"), rowIndex !== null ? parseInt(rowIndex, 10) : null);
      });
    });

    // inputs (text/select/textarea/checkbox) — onChange
    app.querySelectorAll("[data-webink-change]").forEach(function (el) {
      var evt = el.type === "checkbox" || el.tagName === "SELECT" ? "change" : "input";
      el.addEventListener(evt, function () {
        var v = el.type === "checkbox" ? el.checked : el.value;
        fireEvent(el.getAttribute("data-webink-change"), v);
      });
    });

    // submit (Enter on a text input, or a form submit) — onSubmit
    app.querySelectorAll("input[data-webink-submit]").forEach(function (el) {
      el.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
          e.preventDefault();
          fireEvent(el.getAttribute("data-webink-submit"), el.value);
        }
      });
    });
    app.querySelectorAll("form[data-webink-submit]").forEach(function (form) {
      form.addEventListener("submit", function (e) {
        e.preventDefault();
        var first = form.querySelector("input, textarea, select");
        fireEvent(form.getAttribute("data-webink-submit"), first ? first.value : null);
      });
    });

    // charts
    app.querySelectorAll("canvas[data-webink-chart]").forEach(function (canvas) {
      var prev = charts.get(canvas);
      if (prev) { try { prev.destroy(); } catch (e) {} }
      var cfg;
      try { cfg = JSON.parse(canvas.getAttribute("data-webink-chart")); } catch (e) { cfg = null; }
      if (cfg && window.Chart) {
        if (!cfg.options) cfg.options = {};
        if (cfg.options.responsive === undefined) cfg.options.responsive = true;
        if (cfg.options.maintainAspectRatio === undefined) cfg.options.maintainAspectRatio = false;
        charts.set(canvas, new window.Chart(canvas, cfg));
      }
    });
  }

  // Modal interactions are delegated so the fresh DOM produced by each render needs no per-node wiring.
  document.addEventListener("click", function (event) {
    var backdrop = event.target.matches && event.target.matches("[data-webink-modal]") ? event.target : null;
    if (backdrop && backdrop.getAttribute("data-webink-close-backdrop") === "true") {
      fireEvent(backdrop.getAttribute("data-webink-close"), null);
    }
  });

  document.addEventListener("click", function (event) {
    var close = event.target.closest && event.target.closest("[data-webink-close]");
    if (close && !close.matches("[data-webink-modal]")) {
      event.preventDefault();
      fireEvent(close.getAttribute("data-webink-close"), null);
    }
  });

  document.addEventListener("keydown", function (event) {
    var modal = app.querySelector("[data-webink-modal]");
    if (!modal) return;
    if (event.key === "Escape" && modal.getAttribute("data-webink-close-escape") === "true") {
      event.preventDefault();
      fireEvent(modal.getAttribute("data-webink-close"), null);
      return;
    }
    if (event.key !== "Tab") return;
    var focusable = modal.querySelectorAll("button, [href], input, select, textarea, [tabindex]:not([tabindex='-1'])");
    if (!focusable.length) { event.preventDefault(); return; }
    var first = focusable[0], last = focusable[focusable.length - 1];
    if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
    else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
  });

  window.addEventListener("popstate", function () {
    renderRoute(location.pathname);
  });

  // Wait for Tailwind's runtime to be ready enough, then render the initial route.
  renderRoute(location.pathname || "/");
})();
