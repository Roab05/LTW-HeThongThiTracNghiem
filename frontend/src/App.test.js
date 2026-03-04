import { render, screen } from '@testing-library/react';
import App from './App';

test('renders login button', () => {
  render(<App />);
  const btn = screen.getByRole('button', { name: /đăng nhập/i });
  expect(btn).toBeInTheDocument();
});
