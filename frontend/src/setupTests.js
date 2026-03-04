// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom';

// Some dependencies (e.g. jspdf) expect TextEncoder/TextDecoder.
// CRA/Jest environment may not provide them by default.
// eslint-disable-next-line no-undef
if (typeof TextEncoder === 'undefined') {
    // eslint-disable-next-line global-require
    const { TextEncoder, TextDecoder } = require('util');
    // eslint-disable-next-line no-undef
    global.TextEncoder = TextEncoder;
    // eslint-disable-next-line no-undef
    global.TextDecoder = TextDecoder;
}
