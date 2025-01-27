import ReactDOM from 'react-dom/client'
// import reportWebVitals from './test/reportWebVitals';
import { RecoilRoot } from 'recoil'
import Initializer from './page/Initializer'

const root = ReactDOM.createRoot(document.getElementById('root')!!)
root.render(
  // <React.StrictMode>
  <RecoilRoot>
    <Initializer />
  </RecoilRoot>
  // </React.StrictMode>
)

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();
