function TextBox({
  placeholder,
  value,
  setValue,
  className,
  style,
  type,
  pattern,
  maxLength,
  onClick,
  onFocus,
  onKeyUp,
}: {
  placeholder: string
  value: string
  setValue: (value: string, event: React.ChangeEvent<HTMLInputElement>) => void
  className?: string
  style?: React.CSSProperties
  type?: string
  pattern?: string
  maxLength?: number
  onClick?: (evt: React.MouseEvent<HTMLInputElement>) => void
  onFocus?: (evt: React.FocusEvent<HTMLInputElement>) => void,
  onKeyUp?: (e: React.KeyboardEvent<HTMLInputElement>) => void,
}) {
  return (
    <div className="text-box">
      <input
        type={type ?? 'text'}
        placeholder={placeholder}
        value={value}
        onChange={(e) => setValue(e.target.value, e)}
        className={className}
        style={style}
        pattern={pattern}
        maxLength={maxLength}
        onClick={onClick}
        onFocus={onFocus}
        onKeyUp={onKeyUp}
      />
    </div>
  )
}

export default TextBox
