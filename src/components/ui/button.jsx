import * as React from "react";

/**
 * Botão simples, sem dependência do shadcn/ui CLI.
 * Todo o visual (cor, borda, sombra) vem das classes Tailwind passadas via `className`
 * em cada tela — este componente só garante o comportamento e o reset base.
 */
const Button = React.forwardRef(({ className = "", type = "button", ...props }, ref) => {
  return (
    <button
      ref={ref}
      type={type}
      className={`inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-sm text-sm font-medium px-4 py-2 transition-colors disabled:pointer-events-none disabled:opacity-50 ${className}`}
      {...props}
    />
  );
});
Button.displayName = "Button";

export { Button };
