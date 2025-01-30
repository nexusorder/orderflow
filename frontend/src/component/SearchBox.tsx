import { BsArrowLeft } from 'react-icons/bs';
import '../asset/css/Components.css';
import { useNavigate } from 'react-router-dom';


function SearchBox({
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
  onChange,
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
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void,
}) {

  const navigate = useNavigate();

  const clickPrev = () => {
    navigate('/');
  }

  return (
    <div className="search-box">
      <div className="prev" onClick={clickPrev}><BsArrowLeft /></div>
      <input type="text"
      autoFocus={true}
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
      onChangeCapture={onChange} />
    </div>
  )
}

export default SearchBox;
