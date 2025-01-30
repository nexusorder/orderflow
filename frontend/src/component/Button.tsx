function Button({
  value,
  className,
  style,
  onClick,
  onKeyUp,
  disabled,
}: {
  value: string
  onClick?: () => void,
  onKeyUp?: (e: React.KeyboardEvent<HTMLInputElement>) => void,
  className?: string
  style?: React.CSSProperties
  disabled?: boolean
}) {
  return (
    <div className="button">
      <input
        type="button"
        value={value}
        onClick={() => onClick!()}
        onKeyUp={(e) => onKeyUp!(e)}
        className={className}
        style={style}
        disabled={disabled}
      />
    </div>
  )
}

export default Button
