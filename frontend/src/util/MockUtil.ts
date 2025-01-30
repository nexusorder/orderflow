import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import categories from '../asset/mock/categories.json';
import products from '../asset/mock/products.json';
import shops from '../asset/mock/shops.json';

let logged = false;
let db: {
    'orders': any[]
} = {
    'orders': []
};
let login = false;

export function mockAxios() {
    if (process.env.REACT_APP_ENV !== 'mock') {
        return;
    }
    const mock = new MockAdapter(axios);

    mock.onPost('/api/v1/auth/login').reply(config => {
        login = true;
        return [200, {
            "success":true,
            "data":true,
            "errorCode":null,
            "errorMessage":null
        }];
    });

    mock.onPost('/api/v1/auth/signup').reply(config => {
        login = true;
        return [200, {
            "success":true,
            "data":true,
            "errorCode":null,
            "errorMessage":null
        }];
    });

    mock.onPost('/api/v1/auth/logout').reply(config => {
        login = false;
        return [200, {
            "success":true,
            "data":true,
            "errorCode":null,
            "errorMessage":null
        }];
    });

    mock.onGet('/api/v1/auth/profile').reply(config => {
        return [200, {
            "success": true,
            "data": login ? {
                "login": "test",
                "name": "tester",
                "nickname": "테스터",
                "email": "tester@orderflow.io",
                "phone": "010-8765-4321",
                "address": "경기 성남시 분당구 대왕판교로 384 (백현동) 11",
                "latitude": 37.3954951,
                "longitude": 127.1103645,
                "seller": false
            } : null,
            "errorCode": null,
            "errorMessage": null
        }];
    });

    mock.onGet('/api/v1/categories').reply(200, {
        "success": true,
        "data": categories.map(it => ({
            key: it.key,
            name: it.name,
            imageUrl: it.imageUrl,
            order: it.order
        })),
        "errorCode": null,
        "errorMessage": null
    });

    mock.onGet('/api/v1/shops/recommend').reply(200, {
        "success": true,
        "data": shops
            .sort(() => Math.random() - 0.5)
            .slice(0, 3).map(it => ({
                id: it.id,
                name: it.name,
                address: it.address,
                phone: it.phone,
                imageUrl: it.imageUrl,
                isOpened: true,
                openTime: it.openTime,
                closeTime: it.closeTime,
                category: it.category,
                products: [],
                minimumOrder: it.minimumOrder,
                deliveryFee: it.deliveryFee,
                rating: it.rating,
                latitude: it.latitude,
                longitude: it.longitude
            })),
        "errorCode": null,
        "errorMessage": null
    });

    mock.onGet(/\/api\/v1\/shops\/\d+/).reply(config => {
        const id = config.url!.match(/\/api\/v1\/shops\/(\d+)/)![1];
        const found = shops.find(it => it.id === id);
        if (found) {
            return [200, 
                {
                    "success": true,
                    "data": {
                        id: found.id,
                        name: found.name,
                        address: found.address,
                        phone: found.phone,
                        imageUrl: found.imageUrl,
                        isOpened: true,
                        openTime: found.openTime,
                        closeTime: found.closeTime,
                        category: found.category,
                        products: products.filter(product => product.shopId === found.id),
                        minimumOrder: found.minimumOrder,
                        deliveryFee: found.deliveryFee,
                        rating: found.rating,
                        latitude: found.latitude,
                        longitude: found.longitude
                    },
                    "errorCode": null,
                    "errorMessage": null
                }
            ];
        } else {
            return [404, undefined]
        }
    });
    
    mock.onPost('/api/v1/search').reply(config => {
        const data = JSON.parse(config.data!);
        const name = data['name'];
        const category = data['category'];
        const found = shops.filter(it => 
            (name && it.name.includes(name)) 
            || (category && it.category === category)
            || (products.find(product => product.shopId === it.id && product.name.includes(name) ))
        );
        return [200, {
            "success": true,
            "data": found.map(shop => ({
                id: shop.id,
                name: shop.name,
                address: shop.address,
                phone: shop.phone,
                imageUrl: shop.imageUrl,
                isOpened: true,
                openTime: shop.openTime,
                closeTime: shop.closeTime,
                category: shop.category,
                products: products.filter(product => product.shopId === shop.id),
                minimumOrder: shop.minimumOrder,
                deliveryFee: shop.deliveryFee,
                rating: shop.rating,
                latitude: shop.latitude,
                longitude: shop.longitude
            })),
            "errorCode": null,
            "errorMessage": null
        }];
    });

    mock.onPost('/api/v1/orders').reply(config => {
        const data = JSON.parse(config.data!);
        const entity = {
            ...data,
            id: (db['orders'].length + 1).toString(),
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            version: 0,
            shop: shops.find(it => it.id === data.shopId),
            products: data.products.map((product: any) => ({
                ...product,
                price: products.find(it => it.id === product.productId)?.price,
                name: products.find(it => it.id === product.productId)?.name,
            })),
            deliveryAddress: '경기 성남시 분당구 대왕판교로 384 (백현동) 11',
            deliveryPhone: '010-8765-4321',
            deliveryFee: shops.find(it => it.id === data.shopId)?.deliveryFee ?? 0,
            grandTotal: (shops.find(it => it.id === data.shopId)?.deliveryFee ?? 0)
                + data.products.map((product: any) => (products.find(it => it.id === product.productId)?.price ?? 0) * product.quantity).reduce((acc: number, cur: number) => acc + cur, 0)
        }
        db['orders'].push(entity);
        return [200, {
            "success": true,
            "data": entity.id,
            "errorCode": null,
            "errorMessage": null
        }];
    });

    mock.onGet('/api/v1/orders').reply(config => {
        return [200, {
            "success": true,
            "data": db['orders'],
            "errorCode": null,
            "errorMessage": null
        }];
    });

    mock.onGet(/\/api\/v1\/orders\/\d+/).reply(config => {
        const id = config.url!.match(/\/api\/v1\/orders\/(\d+)/)![1];
        return [200, {
            "success": true,
            "data": db['orders'].find(it => it.id === id),
            "errorCode": null,
            "errorMessage": null
        }];
    });

    if (!logged) {
        console.log('%caxios %cmocked', 'color: skyblue;', 'color: inherit;');
        logged = true;
    }
}

