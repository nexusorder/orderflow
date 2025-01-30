import { BsSearch } from 'react-icons/bs';
// https://react-icons.github.io/react-icons/icons/bs/
import '../asset/css/Components.css';
import { useEffect, useState } from 'react';
import { useRecoilState } from 'recoil';
import { profileAtom } from '../service/AuthService';
import { useNavigate } from 'react-router-dom';

function StaticSearchBox() {
  const [profileStatus, setProfileStatus] = useRecoilState(profileAtom);
  
  const nickname = profileStatus.nickname ?? '고객';
  const [textIndex, setTextIndex] = useState(0);
  const texts = ['치킨 어때요?', '피자 어때요?', '버거 어때요?', '곱창 어때요?', '마라탕 어때요?', '초밥 어때요?', '짜장면 어때요?', '떡볶이 어때요?', '족발 어때요?', '보쌈 어때요?', '김밥 어때요?',  '쌀국수 어때요?'];

  const navigate = useNavigate();

  useEffect(() => {
    const interval = setInterval(() => {
      setTextIndex((prevIndex) => (prevIndex + 1) % texts.length);
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  const clickSearch = () => {
      navigate('/search');
  };

  return (
    <div className="static-search-box" onClick={clickSearch}>
      <div className='search-icon'>
        <BsSearch />
      </div>
      <div className='search-text'>
        <div className='nickname'>{nickname}님, </div>
        <div className='rotating-text'>{texts[textIndex]}</div>
      </div>
    </div>
  )
}

export default StaticSearchBox
