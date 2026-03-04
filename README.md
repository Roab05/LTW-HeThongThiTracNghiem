# LTW-HeThongThiTracNghiem

A simple frontend project for the online testing system used in the Web Programming course.  The repository currently contains static files (`index.html`, `style.css`, `script.js`), and can be extended into a React-based application.

---

## 📦 Prerequisites

Before setting up the project, make sure you have:

1. **Node.js (>=14.x)** and **npm** installed.  Download from [nodejs.org](https://nodejs.org/).
2. A code editor such as **VS Code**.
3. (Optional) A basic HTTP server if you only use the static version, e.g. `http-server` or the Live Server extension.

---

## 🚀 Setup Instructions

You can work with this project in two different modes:

### 1. Quick start with CDN (no build system, ae kệ bước này đi, xuống xem cách 2)

This mode is handy for experimentation or classroom demos. It uses React via CDN and Babel for in‑browser JSX compilation.

1. Open `index.html` and ensure it contains the following resources inside the `<head>`:

    ```html
    <script src="https://unpkg.com/react@18/umd/react.development.js" crossorigin></script>
    <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js" crossorigin></script>
    <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
    ```

2. Add a root element in the `<body>`:

    ```html
    <div id="root"></div>
    <script type="text/babel" src="script.js"></script>
    ```

3. Write React components in `script.js` and run the file by simply opening `index.html` in your browser or using a lightweight server.

> ⚠️ This setup is **not suitable for production**. It downloads large dev builds and uses Babel at runtime.

### 2. Full project with npm & bundler (ae làm theo cái này nhé)

For a proper development workflow you should convert this into a Node project with a bundler (CRA/Vite/Webpack).

1. Open a terminal in the project root and initialise npm:

    ```bash
    npm init -y
    ```

2. Install core dependencies:

    ```bash
    npm install react react-dom
    ```

3. Add a build system. The easiest is to scaffold a new Create React App and adapt the source files:

    ```bash
    npx create-react-app frontend
    cd frontend
    npm start
    ```

    You can also manually configure webpack or switch to [Vite](https://vitejs.dev/) if desired.

4. Move your existing HTML/CSS/JS into the `frontend/src` directory as components:

    - `public/index.html` should have `<div id="root"></div>`.
    - `src/App.js` contains the root component.
    - Use `import './style.css';` for styles.

5. Run the development server:

    ```bash
    npm start
    ```

6. Build for production:

    ```bash
    npm run build
    ```

---

## 🗂 Directory Structure

```text
LTW-HeThongThiTracNghiem/
├─ index.html        # static entrypoint (CDN mode)
├─ script.js         # main script file
├─ style.css         # stylesheet
├─ package.json      # added when npm init run
└─ frontend/         # optional CRA/Vite project
   ├─ public/
   └─ src/
```

---

## 📝 Usage

1. Clone the repository.
2. Choose a mode (CDN or npm).
3. Follow the corresponding setup steps above.
4. Develop your interface within React components.
5. Use a local web server to serve the files or rely on CRA's built-in server.

---

## 🤝 Contributing

Feel free to fork the repository and submit pull requests. Maintain clean commits and update this README when you add new dependencies or build configurations.

---

## 📄 License

This project is provided for educational purposes only.  Check with your course instructor for reuse policies.
