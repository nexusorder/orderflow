import {
  BsHouseDoor,
  BsSearch,
  BsClipboard2Check,
  BsPerson,
} from 'react-icons/bs'
// https://react-icons.github.io/react-icons/icons/bs/
import '../asset/css/Components.css'
import { useNavigate } from 'react-router-dom';

function TabBar({ activeTab }: { activeTab: number }) {
  const tabs = [
    {
      icon: <BsHouseDoor />,
      text: '홈',
      path: '/',
    },
    {
      icon: <BsSearch />,
      text: '검색',
      path: '/search',
    },
    {
      icon: <BsClipboard2Check />,
      text: '주문내역',
      path: '/orders',
    },
    {
      icon: <BsPerson />,
      text: '내 정보',
      path: '/profile',
    },
  ]
  const navigate = useNavigate();

  const tabClick = (index: number) => {
    navigate(tabs[index].path)
  }

  return (
    <div className="tab-bar">
      {tabs.map((tab, index) => (
        <div
          className={'tab' + (activeTab === index ? ' active' : '')}
          key={index}
          onClick={(_) => (activeTab !== index ? tabClick(index) : null)}
          style={{ cursor: activeTab !== index ? 'pointer' : 'default' }}
        >
          <div className="tab-icon">{tab.icon}</div>
          <div className="tab-text">{tab.text}</div>
        </div>
      ))}
    </div>
  )
}

export default TabBar
