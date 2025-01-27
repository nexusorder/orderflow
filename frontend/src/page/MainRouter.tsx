import { Route, BrowserRouter as Router, Routes } from 'react-router-dom'
import '../asset/css/index.css'
import Cart from './Cart'
import Home from './Home'
import Login, { LoginComponentType } from './Login'
import Orders from './Orders'
import Profile from './Profile'
import Search from './Search'
import Shop from './Shop'

function MainRouter() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route
          path="/login"
          element={<Login type={LoginComponentType.LOGIN} />}
        />
        <Route
          path="/signup"
          element={<Login type={LoginComponentType.SIGNUP} />}
        />
        <Route path="/search" element={<Search type='search'/>} />
        <Route path="/category/*" element={<Search type='category'/>} />
        <Route path="/shop/*" element={<Shop />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/orders/*" element={<Orders />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/profile" element={<Profile />} />
        <Route
          path="/*"
          element={<div style={{ color: 'white' }}>not found</div>}
        />
      </Routes>
    </Router>
  )
}

export default MainRouter
