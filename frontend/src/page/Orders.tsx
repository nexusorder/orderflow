import axios from 'axios'
import { useEffect, useState } from 'react'
import '../asset/css/Order.css'
import TabBar from '../component/TabBar'
import { useNavigate } from 'react-router-dom'
import { formatNumber, orderOne, ordersAll, prepareOrder } from '../service/OrderService'
import { BsArrowLeft } from 'react-icons/bs'
import { BsClipboard } from "react-icons/bs";

function Orders() {
  const client = axios.create({
    baseURL: '',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  const [order, setOrder] = useState<any | null>(null)
  const [orderId, setOrderId] = useState('')
  const [orders, setOrders] = useState<any>([])
  const [mode, setMode] = useState('')

  const navigate = useNavigate();
  
  useEffect(() => {
    if (window.location.pathname === '/orders') {
      setMode('all');
      setOrderId('');
      ordersAll().then((it: any) => {
        if (it?.data?.data ?? false) {
          const preparedOrders = it.data.data.map(prepareOrder);
          setOrders(preparedOrders);
        }
      }).catch((error: any) => {
        navigate('/login')
      });
    } else {
      setMode('detail');
      const parsedOrderId = window.location.pathname.split('/').pop() ?? ''
      setOrderId(parsedOrderId);
      orderOne(parsedOrderId).then((it: any) => {
        if (it?.data?.data ?? false) {
          const preparedOrder = prepareOrder(it.data.data);
          setOrder(preparedOrder);
        }
      }).catch((error: any) => {
        navigate('/orders')
      });
    }
  }, [window.location.pathname]);

  const handleOrderClick = (id: string) => {
    navigate(`/orders/${id}`);
  }

  const getAllComponent = () => {
    return (
      <div className='wrapper'>
        <div className='header'>주문내역</div>
        <div className="orders">
          {orders.map((order: any, index: number) => {
            return (
              <div key={index} className="item" onClick={() => handleOrderClick(order.id)}>
                <div className="order-created-at quiet">{order.createdAt}</div>
                <div className="order-summary">
                  <div className='left'>
                    <div className="image-wrapper">
                      {order?.shop?.imageUrl && <img className='image' src={order?.shop?.imageUrl} alt="image"></img>}
                    </div>
                  </div>
                  <div className='right'>
                    <div className="shop-name">{order?.shop?.name}</div>
                    <div className="grand-total">{order.grandTotal}원</div>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </div>);
  };
  const getEmptyComponent = () => {
    return (
      <div className='wrapper'>
        <div className='header'>주문내역</div>
        <div className="empty">
          <div className="icon-wrapper">
            <BsClipboard className="icon"/>
          </div>
          <div className="label">주문내역이 없습니다.</div>
        </div>
      </div>
    )
  };

  const getDetailComponent = () => {
    return <div className="wrapper">
        <div className="top-layer">
          <BsArrowLeft
            className="prev"
            onClick={() => {
              navigate('/orders')
            }}
          />
        </div>
        <div className='header'>주문상세</div>
        <div className="order-info">
          <div className="shop-name">{order?.shop?.name}</div>
          <div className="order-created-at quiet">주문일시: {order?.createdAt}</div>
          <div className="order-id quiet">주문번호: {order?.id}</div>
        </div>
        <div className="order-products">
          {order?.products.map((product: any, index: number) => {
            return (
              <div key={index} className="product">
                <div className='left'>
                  <div className="image-wrapper">
                    {product.imageUrl && <img className='image' src={product.imageUrl} alt="image"></img>}
                  </div>
                </div>
                <div className='middle'>
                  <div className="name">{product.name}</div>
                  <div className="price">{formatNumber(Number(product.price.replaceAll(',', '')) * product.quantity)}</div>
                </div>
                <div className='right'>
                  <div className="quantity">{product.quantity}</div>
                </div>
              </div>
            )
          })}
        </div>
        <div className="order-summary">
          <div className="row">
            <div className="label">총 금액</div>
            <div className="value">{formatNumber(Number(order?.grandTotal?.replaceAll(',', '')) - Number(order?.deliveryFee.replaceAll(',', '')))}원</div>
          </div>
          <div className="row">
            <div className="label">배달비</div>
            <div className="value">{order?.deliveryFee}원</div>
          </div>
          <div className="row grand-total">
            <div className="label">총 주문 금액</div>
            <div className="value">{order?.grandTotal}원</div>
          </div>
        </div>
        <div className="delivery-summary">
          <div className="row">
            <div className="label quiet">배달주소</div>
            <div className="value">{order?.deliveryAddress}</div>
          </div>
          <div className="row">
            <div className="label quiet">전화번호</div>
            <div className="value">{order?.deliveryPhone}</div>
          </div>
        </div>
    </div>
  };

  return (
    <div className="order">
      <div className='container scrollable'>
        {mode === 'all' && orders.length !== 0 && getAllComponent()}
        {mode === 'all' && orders.length === 0 && getEmptyComponent()}
        {mode === 'detail' && getDetailComponent()}
      </div>
      <TabBar activeTab={2} />
    </div>
  )
}

export default Orders
