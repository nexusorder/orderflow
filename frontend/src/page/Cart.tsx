import { useEffect, useState } from 'react'
import { useRecoilState } from 'recoil'
import '../asset/css/Cart.css'
import locationService, { locationAtom } from '../service/LocationService'
import { shopOne, formatNumber, prepareShop } from '../service/ShopService'
import { BsArrowLeft, BsFillStarFill, BsTrash } from 'react-icons/bs'
import { cartAtom, addProductToCart } from '../service/CartService'
import {useNavigate} from 'react-router-dom'
import { createOrder } from '../service/OrderService'

function Cart() {
  
  const [shop, setShop] = useState<any | null>(null)
  const [shopId, setShopId] = useState('')
  const [locationStatus, _] = useRecoilState(locationAtom)
  const [cartStatus, setCartStatus] = useRecoilState(cartAtom)
  const [grandTotal, setGrandTotal] = useState(0);

  const navigate = useNavigate();
  
  useEffect(() => {
    if (((cartStatus?.shopId ?? '') === '') || (cartStatus?.list ?? []).length === 0) {
      if (window.history.length > 1) {
        navigate(-1)
      } else {
        navigate('/');
      }
      return;
    }
    const parsedShopId = cartStatus.shopId;
    setShopId(parsedShopId);
    shopOne(parsedShopId).then((it: any) => {
      if (it?.data?.data ?? false) {
        const shop = prepareShop(it.data.data, locationStatus, locationService);
        setShop(shop)
      }
    });
  }, [])

  useEffect(() => {
    if (!shop) {
      return;
    }
    const value = cartStatus.totalPrice + Number(shop.deliveryFee.replace(',', '') ?? 0);
    setGrandTotal(value);
  }, [cartStatus, shop]);

  const handleItemQuantityClick = (product: any, delta: number) => {
    addProductToCart({
      shopId: shopId,
      productId: product.productId, 
      quantity: delta, 
      price: product.price, 
      setCartStatus
    });
  }

  const handleOrderClick = () => {
    if (grandTotal < Number(shop.minimumOrder.replace(',', ''))) {
      return;
    }
    createOrder({
      shopId: shopId,
      products: cartStatus.list.map((it: any) => {
        return {
          productId: it.productId,
          quantity: it.quantity,
        }
      })
    }).then((it: any) => {
      if (it?.data?.data ?? false) {
        setCartStatus({'list': [], 'totalPrice': 0, 'totalQuantity': 0, 'shopId': ''});
        navigate(`/orders/${it.data.data}`);
      }
    }).catch((err: any) => {
      if (err.response.status === 401) {
        navigate('/login');
        return;
      }
    })
  };

  if (!shop || ((cartStatus?.shopId ?? '') === '') || (cartStatus?.list ?? []).length === 0) {
    return <div className="cart">
      <div className="container scrollable">
      </div>
    </div>;
  }

  return (
    <div className="cart">
      <div className="container scrollable">
        <div className="top-layer">
          <BsArrowLeft
            className="prev"
            onClick={() => {
              if (window.history.length > 1) {
                navigate(-1)
              } else {
                navigate('/');
              }
            }}
          />
        </div>
        <div className="header">
          장바구니
        </div>
        <div className="wrapper">
          <div className="shop-header">
            <div className="row shop-info">
              <div className="shop-image">
                <img className="image" src={shop.imageUrl} alt="image"></img>
              </div>
              <div className="name">{shop.name}</div>
            </div>
          </div>
          <div className="product-wrapper">
            <div className="list">
              {cartStatus.list?.map((cartItem: any, index: number) => (
                <div key={index} className="item">

                  <div className="left">
                    <div className="image-wrapper">
                      {cartItem.imageUrl &&
                        <img className='image' src={cartItem.imageUrl} alt="image"></img>
                      }
                    </div>
                  </div>
                  <div className="middle">
                    <div className="name">{shop.products.filter((it: any) => it.id === cartItem.productId)[0]?.name}</div>
                    <div className="price">{formatNumber(cartItem.price * cartItem.quantity)}원</div>
                  </div>
                  <div className="right">
                    <div className='quantity-wrapper'>
                      <div className="minus" onClick={_ => handleItemQuantityClick(cartItem, -1)}>{cartItem.quantity <= 1 ? <BsTrash className='trash'/> : '-' }</div>
                      <div className="quantity">{cartItem.quantity}</div>
                      <div className="plus"  onClick={_ => handleItemQuantityClick(cartItem, +1)}>+</div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div className="summary">
            <div className="row header">
              결제금액을 확인해주세요
            </div>
            <div className="row">
              <div className="label">총 금액</div>
              <div className="value">{formatNumber(cartStatus.totalPrice)}원</div>
            </div>
            <div className="row">
              <div className="label">배달비</div>
              <div className="value">{shop.deliveryFee}원</div>
            </div>
            <div className="row grand-total">
              <div className="label">결제예정금액</div>
              <div className="value">{formatNumber(grandTotal)}원</div>
            </div>
          </div>
        </div>
      </div>
      <div className='order-wrapper'>
        <div className='order-button' onClick={handleOrderClick} aria-disabled={grandTotal < Number(shop.minimumOrder.replace(',', ''))}>
          <div className='total-price'>{formatNumber(grandTotal)}원</div>
          <div className='middot'>&middot;</div>
          <div className='label'>주문하기</div>
          <div className='total-quantity'>
            <span className='label'>{cartStatus.totalQuantity}</span>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Cart;
