import { BsFillStarFill } from 'react-icons/bs'
import { useNavigate } from 'react-router-dom'
import { cartAtom } from '../service/CartService';
import { useRecoilState } from 'recoil';
import { CSSProperties } from 'react';

function ShopItem({shop, hideUnderbar, style}: { shop: any, hideUnderbar?: boolean, style?: CSSProperties}) {
    const [cartStatus, setCartStatus] = useRecoilState(cartAtom);
    const navigate = useNavigate();

    const handleShopClick = () => {
        if (shop.id === '') {
            return;
        }
        if (cartStatus.shopId !== '' && cartStatus.shopId !== shop.id) {
            setCartStatus((prev: any) => {
                return {
                    list: [],
                    totalPrice: 0,
                    totalQuantity: 0,
                    shopId: shop.id,
                };
            });
        }
        navigate(`/shop/${shop.id}`);
    };

    return <div className="shop" style={hideUnderbar ? {...(style ?? {}), borderBottom: 'none' } : (style ?? {})} onClick={() => handleShopClick()}>
        <div className="row image">
        <img className="image" src={shop.imageUrl} alt="image" />
        </div>
        <div className="row">
        <div className="name">{shop.name}</div>
        <div className="char">
            <BsFillStarFill className="star" />
        </div>
        <div className="rating">{shop.rating.toFixed(1)}</div>
        {/* ({shop.reviews}) */}
        </div>
        <div className="row sub">
        <div className="delay">{shop.delay}분</div>
        <div className="middot quiet"> &middot; </div>
        <div className="minimum">
            <span className="quiet">최소주문</span>
            <span>{shop.minimumOrder}원</span>
        </div>
        </div>
    </div>;
};

export default ShopItem;