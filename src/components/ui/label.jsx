import * as React from "react";

const Label = React.forwardRef(({ className = "", ...props }, ref) => (
  <label
    ref={ref}
    className={`block text-sm font-medium leading-none ${className}`}
    {...props}
  />
));
Label.displayName = "Label";

export { Label };
