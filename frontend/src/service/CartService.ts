import axios from 'axios'
import { atom } from 'recoil';
import { mockAxios } from '../util/MockUtil';

// client
mockAxios();
const client = axios.create({
  baseURL: '',
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

// atoms
export const cartAtom = atom({
  key: 'cart/cart',
  default: {'list': [], 'totalPrice': 0, 'totalQuantity': 0, 'shopId': ''},
});

// methods
export function cart() {
  return client.get('/api/v1/cart')
}

// business logic
export function addProductToCart(
  {shopId, productId, quantity, price, setCartStatus}
   : {shopId: string, productId: string, quantity: number, price: number, setCartStatus: any})
  {
    if (shopId === '' || productId === '' || price <= 0) {
      return;
    }
  const data = {
    productId,
    quantity,
    price,
  }

  setCartStatus((prev: any) => {
    const newList = shopId === prev.shopId ? [...prev.list] : []
    const index = newList.findIndex(item => item.productId === productId)
    if (index === -1) {
      newList.push(data)
    } else {
      const newItem = {
        ...newList[index],
        quantity: newList[index].quantity + quantity,
      }
      if (newItem.quantity <= 0) {
        newList.splice(index, 1)
      } else {
        newList[index] = newItem;
      }
    }
    const totalPrice = newList.reduce((acc, cur) => {
      return acc + cur.price * cur.quantity;
    }, 0);
    const totalQuantity = newList.reduce((acc, cur) => {
      return acc + cur.quantity;
    }, 0);

    return {
      list: newList,
      shopId,
      totalPrice,
      totalQuantity,
    };
  });
}