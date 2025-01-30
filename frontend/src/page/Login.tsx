import axios from 'axios'
import { useEffect, useState } from 'react'
import { BsArrowLeft } from 'react-icons/bs'
import { useNavigate } from 'react-router-dom'
import { useRecoilState } from 'recoil'
import '../asset/css/Login.css'
import Button from '../component/Button'
import TextBox from '../component/TextBox'
import { login, profile, profileAtom, signup } from '../service/AuthService'

export enum LoginComponentType {
  LOGIN = 'LOGIN',
  SIGNUP = 'SIGNUP',
}

interface daum {
  Postcode: any
}

function Login({ type }: { type: LoginComponentType }) {
  const client = axios.create({
    baseURL: '',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  const daum: daum = (window as any).daum

  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')

  // for signup
  const [passwordConfirm, setPasswordConfirm] = useState('')
  const [name, setName] = useState('')
  const [nickname, setnickname] = useState('')
  const [phone, setPhone] = useState('010-')
  const [address, setAddress] = useState('')
  const [email, setEmail] = useState('')
  const [disabled, setDisabled] = useState(true)

  const [addressJustClosed, setAddressJustClosed] = useState(false)
  const [addressOpened, setAddressOpened] = useState(false)
  const [addressElement, setAddressElement] = useState<HTMLInputElement | undefined>(undefined)

  const [showLoginWarning, setShowLoginWarning] = useState(false)

  const [profileStatus, setProfileStatus] = useRecoilState(profileAtom)

  const limits = {
    id: { min: 4, max: 32 },
    password: { min: 8, max: 256 },
    name: { min: 2, max: 32 },
    nickname: { min: 2, max: 32 },
    phone: { min: 6, max: 14 },
    email: { min: 7, max: 128 },
    address: { min: 6, max: 256 },
  }

  const navigate = useNavigate();

  useEffect(() => {
    
  }, []);

  useEffect(() => {
    setDisabled(!isValid())
  }, [loginId, password, passwordConfirm, name, phone, address, email])

  useEffect(() => {
    if (addressJustClosed) {
      setAddressJustClosed(false);
      if (addressElement) {
        addressElement.selectionStart = addressElement.selectionEnd = addressElement.value.length
        addressElement.focus();
      }
    }
  }, [address]);

  const isValid = () => {
    return (
      loginId.trim() !== '' &&
      loginId.length >= limits['id'].min &&
      loginId.length <= limits['id'].max &&
      password.trim() !== '' &&
      password.length >= limits['password'].min &&
      password.length <= limits['password'].max &&
      passwordConfirm.trim() !== '' &&
      passwordConfirm.length >= limits['password'].min &&
      passwordConfirm.length <= limits['password'].max &&
      password === passwordConfirm &&
      name.trim() !== '' &&
      name.length >= limits['name'].min &&
      name.length <= limits['name'].max &&
      nickname.trim() !== '' &&
      nickname.length >= limits['nickname'].min &&
      nickname.length <= limits['nickname'].max &&
      phone.trim() !== '' &&
      phone.length >= limits['phone'].min &&
      phone.length <= limits['phone'].max &&
      address.trim() !== '' &&
      address.length >= limits['address'].min &&
      address.length <= limits['address'].max &&
      email.trim() !== '' &&
      email.length >= limits['email'].min &&
      email.length <= limits['email'].max
    )
  }

  const doLogin = () => {
    if (loginId.trim() === '' || password.trim() === '') {
      setShowLoginWarning(true)
      return
    }
    login({ login: loginId, password })
      .then((res) => {
        if (res.data.data) {
          setShowLoginWarning(false)
          return profile()
            .then((res2) => {
              console.log(`%clogin %ctrue`, 'color: skyblue;', 'color: inherit;')
              setProfileStatus(res2.data.data)
              navigate('/')
            });
        } else {
          setShowLoginWarning(true)
        }
      }).catch((err) => {
        setShowLoginWarning(true)
      });
  }

  const doSignup = () => {
    if (!isValid()) {
      return
    }
    signup({
      login: loginId,
      password,
      passwordConfirm,
      name,
      nickname,
      phone,
      email,
      address,
    }).then((res) => {
      if (res.data.data) {
        return profile()
            .then((res2) => {
              console.log(`%clogin %ctrue`, 'color: skyblue;', 'color: inherit;')
              setProfileStatus(res2.data.data)
              navigate('/')
            });
      }
    });
  }

  const setFormattedPhone = (event: any) => {
    event.preventDefault()
    let value = event.target.value.replace(/\D/g, '')
    if (value.length > 3 && value.length <= 7) {
      value = value.slice(0, 3) + '-' + value.slice(3)
    } else if (value.length > 7 && value.length <= 11) {
      value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7)
    } else if (value.length > 11) {
      return
    }
    setPhone(value)
  }

  const onAddressClick = (event: any) => {
    // https://postcode.map.daum.net/guide
    if ((address.trim() ?? '') !== '') {
      return
    }
    setAddressElement(event.target);
    setAddressOpened(true);

    const wrapper = document.querySelector('.address-wrapper')
    const layer = document.querySelector('.address-layer')

    wrapper?.removeAttribute('style')

    new daum.Postcode({
      oncomplete: function (data: any) {
        // 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

        // 각 주소의 노출 규칙에 따라 주소를 조합한다.
        // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
        var addr = '' // 주소 변수
        var extraAddr = '' // 참고항목 변수

        //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
        if (data.userSelectedType === 'R') {
          // 사용자가 도로명 주소를 선택했을 경우
          addr = data.roadAddress
        } else {
          // 사용자가 지번 주소를 선택했을 경우(J)
          addr = data.jibunAddress
        }

        // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
        if (data.userSelectedType === 'R') {
          // 법정동명이 있을 경우 추가한다. (법정리는 제외)
          // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
          if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
            extraAddr += data.bname
          }
          // 건물명이 있고, 공동주택일 경우 추가한다.
          if (data.buildingName !== '' && data.apartment === 'Y') {
            extraAddr +=
              extraAddr !== '' ? ', ' + data.buildingName : data.buildingName
          }
          // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
          if (extraAddr !== '') {
            extraAddr = ' (' + extraAddr + ')'
          }
          // 조합된 참고항목을 해당 필드에 넣는다
          addr += extraAddr
        } else {
        }

        addr += ' '

        // 우편번호와 주소 정보를 해당 필드에 넣는다.
        // document.getElementById('sample2_postcode').value = data.zonecode;
        setAddressJustClosed(true);
        setAddress(addr)

        wrapper?.setAttribute('style', 'display: none;')
        setAddressOpened(false);
      },
      width: '100%',
      height: '100%',
      maxSuggestItems: 10,
    }).embed(layer)
  }

  const warningByLength = (
    value: string,
    text: string,
    minLength: number = 8,
    maxLength: number = 256
  ) => {
    return warningComponent(
      (value.length !== 0 && value.length < minLength) ||
        value.length > maxLength,
        `${text} ${minLength}자 이상 ${maxLength}자 이하여야합니다.`
    )
  }

  const warningComponent = (visible: boolean, message: string) => {
    return visible ? (
      <div
        style={{
          color: 'var(--red1)',
          textAlign: 'left',
          width: '100%',
          marginTop: '0.3rem',
          marginBottom: '0.9rem',
        }}
      >
        {message}
      </div>
    ) : (
      <div style={{ height: '1.2rem', minHeight: '1.2rem' }}></div>
    )
  }

  const loginType = () => {
    return (
      <>
        <div className='logo-wrapper'>
          <img
            src="/asset/image/orderflow-logo-transparent.png"
            alt="logo"
            className="logo"
          />
        </div>
        <div className='form-wrapper'>
          <TextBox
            placeholder="아이디"
            value={loginId}
            setValue={(value) => {
              if (value.length !== 0) {
                setShowLoginWarning(false)
              }
              setLoginId(value)
            }}
            onKeyUp={e => {if (e.key === 'Enter') { doLogin() }}} 
            maxLength={limits['id'].max}
          />
          {warningComponent(showLoginWarning && loginId.length === 0, '아이디를 입력해주세요.')}
          <TextBox
            placeholder="비밀번호"
            value={password}
            setValue={(value) => {
              if (value.length !== 0) {
                setShowLoginWarning(false)
              }
              setPassword(value)
            }}
            type="password"
            onKeyUp={e => {if (e.key === 'Enter') { doLogin() }}} 
            maxLength={limits['password'].max}
          />
          {warningComponent(showLoginWarning, (password.length === 0 ? '비밀번호를 입력해주세요.' : '아이디 또는 비밀번호가 일치하지 않습니다.'))}
          <Button value="로그인" onClick={doLogin}/>
          <div className="seperator">
            <div>
              <hr></hr>
            </div>
            <span>아직 회원이 아니신가요?</span>
            <div>
              <hr></hr>
            </div>
          </div>
          <Button
            value="회원가입"
            onClick={() => {setLoginId(''); setPassword(''); navigate('/signup')}}
            style={{ background: 'var(--gray3)' }}
          />
        </div>
      </>
    )
  }

  const signupType = () => {
    return (
      <>
        <div className="address-wrapper" style={{ display: 'none' }}>
          <div className="header">
            <BsArrowLeft
              className="prev"
              onClick={() => {
                setAddressOpened(false);
                document
                  .querySelector('.address-wrapper')
                  ?.setAttribute('style', 'display: none;')
              }}
            />
          </div>
          <div className="address-layer"></div>
        </div>
        <div className="header" style={{display: (window.history.length !== 0 ? '' : 'none')}}>
            <BsArrowLeft
              className="prev"
              onClick={() => {
                if (window.history.length !== 0) {
                  navigate(-1)
                } else {
                  navigate('/login')
                }
              }}
            />
          </div>
        <h1 style={{ marginBottom: '3rem' }}>반가워요</h1>
        <TextBox
          placeholder="아이디"
          value={loginId}
          setValue={(value) => {
            setLoginId(value)
          }}
          maxLength={limits['id'].max}
        />
        {warningByLength(loginId, '아이디는', limits['id'].min, limits['id'].max)}
        <TextBox
          placeholder="비밀번호"
          value={password}
          setValue={(value) => {
            setPassword(value)
          }}
          type="password"
          maxLength={limits['password'].max}
        />
        {warningByLength(
          password,
          '비밀번호는',
          limits['password'].min,
          limits['password'].max
        )}
        <TextBox
          placeholder="비밀번호 확인"
          value={passwordConfirm}
          setValue={(value) => {
            setPasswordConfirm(value)
          }}
          type="password"
          maxLength={limits['password'].max}
        />
        {warningComponent(password !== '' && password !== passwordConfirm, '비밀번호가 일치하지 않습니다.')}
        <TextBox
          placeholder="이름"
          value={name}
          setValue={(value) => {
            setName(value)
          }}
          maxLength={limits['name'].max}
        />
        {warningByLength(
          name,
          '이름은',
          limits['name'].min,
          limits['name'].max
        )}
        <TextBox
          placeholder="닉네임"
          value={nickname}
          setValue={(value) => {
            setnickname(value)
          }}
          maxLength={limits['nickname'].max}
        />
        {warningByLength(
          nickname,
          '닉네임은',
          limits['nickname'].min,
          limits['nickname'].max
        )}
        <TextBox
          placeholder="휴대폰 번호"
          value={phone}
          setValue={(_, event) => {
            setFormattedPhone(event)
          }}
          pattern="[0-9]{3}-[0-9]{3,4}-[0-9]{4}"
          maxLength={limits['phone'].max}
          style={{ marginBottom: '1.2rem' }}
        />
        <TextBox
          placeholder="이메일"
          value={email}
          setValue={(value) => {
            setEmail(value)
          }}
          type="email"
          maxLength={limits['email'].max}
        />
        {warningByLength(
          email,
          '이메일은',
          limits['email'].min,
          limits['email'].max
        )}
        <TextBox
          placeholder="주소"
          value={address}
          setValue={(value) => {
            setAddress(value)
          }}
          maxLength={limits['address'].max}
          // onClick={(e) => onAddressClick(e)}
          onFocus={(e) => onAddressClick(e)}
        />
        {warningByLength(
          address,
          '주소는',
          limits['address'].min,
          limits['address'].max
        )}
        <Button
          value="등록하기"
          disabled={disabled}
          onClick={doSignup}
          style={{ marginTop: '2.4rem' }}
        />
      </>
    )
  }

  return (
    <div className="home">
      <div className={"login scrollable" + (addressOpened ? " hide" : "")}>
        {type === LoginComponentType.LOGIN ? loginType() : signupType()}
      </div>
    </div>
  )
}

export default Login
