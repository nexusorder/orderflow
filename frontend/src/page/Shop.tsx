import { useEffect, useState } from 'react'
import { BsArrowLeft, BsFillStarFill } from 'react-icons/bs'
import { useLocation, useNavigate } from 'react-router-dom'
import { useRecoilState } from 'recoil'
import '../asset/css/Shop.css'
import TabBar from '../component/TabBar'
import { addProductToCart, cartAtom } from '../service/CartService'
import locationService, { locationAtom } from '../service/LocationService'
import { formatNumber, prepareShop, shopOne } from '../service/ShopService'

function Shop() {
  
  const [shop, setShop] = useState<any | null>(null)
  const [shopId, setShopId] = useState('')
  const [locationStatus, _] = useRecoilState(locationAtom)
  const [cartStatus, setCartStatus] = useRecoilState(cartAtom)

  const navigate = useNavigate();
  const location = useLocation();
  
  useEffect(() => {
    const parsedShopId = window.location.pathname.split('/').pop() ?? ''
    setShopId(parsedShopId);
    shopOne(parsedShopId).then((it: any) => {
      if (it?.data?.data ?? false) {
        const shop = prepareShop(it.data.data, locationStatus, locationService);
        setShop(shop)
      }
    }).catch((error: any) => {
      navigate('/')
    });
  }, [])

  const handleItemClick = (product: any) => {
    addProductToCart({
      shopId: shopId,
      productId: product.id, 
      quantity: 1, 
      price: product.price, 
      setCartStatus
    });
  }

  const handleCartClick = () => {
    navigate('/cart');
  };

  if (!shop) {
    return <div className="shop-detail">
      <div className="container scrollable">
      </div>
    </div>;
  }

  return (
    <div className="shop-detail">
      <div className="container scrollable">
        <div className="top-layer">
          <BsArrowLeft
            className="prev"
            onClick={() => {
              if (window.history.length > 1) {
                if (location.state?.from === 'search') {
                  navigate('/', {replace: true, state: {from: 'shop', keyword: location.state?.keyword}})
                } else {
                  navigate(-1)
                }
              } else {
                navigate('/');
              }
            }}
          />
        </div>
        <div className="shop-image">
          <img className="image" src={shop.imageUrl} alt="image"></img>
        </div>
        <div className="wrapper">
          <div className="shop-header">
            <div className="row">
              <div className="name">{shop.name}</div>
            </div>
            <div className="row ratings">
              <div className="char">
                  <BsFillStarFill className="star" />
              </div>
              <div className="rating">{shop.rating.toFixed(1)}</div>
            </div>
          </div>
          <div className="shop-info">
            <div className="row">
              <div className="label quiet">최소주문금액</div>
              <div className="value">{shop.minimumOrder}원</div>
            </div>
            <div className="row">
              <div className="label quiet">배달비</div>
              <div className="value">{shop.deliveryFee}원</div>
            </div>
            <div className="row">
              <div className="label quiet">배달시간</div>
              <div className="value">{shop.delay}분</div>
            </div>
          </div>
          <div className="product-wrapper">
            <div className="header">
              <div className="title">메뉴</div>
            </div>
            <div className="list">
              {shop.products?.map((product: any, index: number) => (
                <div key={index} className="item" onClick={_ => handleItemClick(product)}>
                  <div className="left">
                    <div className="name">{product.name}</div>
                    <div className="price">{formatNumber(product.price)}원</div>
                  </div>
                  <div className="right">
                    <div className="image-wrapper">
                      {product.imageUrl &&
                        <img className='image' src={product.imageUrl} alt="image"></img>
                      }
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
      { cartStatus.list.length === 0 ? 
        <TabBar activeTab={1} />: 
        <div className='cart-wrapper'>
          <div className='cart-button' onClick={handleCartClick}>
            <div className='total-price'>{formatNumber(cartStatus.totalPrice)}원</div>
            <div className='middot'>&middot;</div>
            <div className='label'>장바구니 보기</div>
            <div className='total-quantity'>
              <span className='label'>{cartStatus.totalQuantity}</span>
            </div>
          </div>
        </div>
        }
    </div>
  )
}

export default Shop;
