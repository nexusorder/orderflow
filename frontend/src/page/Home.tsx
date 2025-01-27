import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useRecoilState } from 'recoil'
import '../asset/css/Home.css'
import ShopItem from '../component/ShopItem'
import StaticSearchBox from '../component/StaticSearchBox'
import TabBar from '../component/TabBar'
import { categories } from '../service/CategoryService'
import locationService, { locationAtom } from '../service/LocationService'
import { prepareShop, recommends } from '../service/ShopService'

function Home() {
  const [categoriesStatus, setCategoriesStatus] = useState([]);
  const [recommenededShops, setRecommenedShops] = useState([]);

  const [locationStatus, _] = useRecoilState(locationAtom);

  const navigate = useNavigate();

  useEffect(() => {
    fetchCategories()
    fetchRecommends()
  }, [])

  const fetchCategories = () => {
    categories().then((it: any) => {
      if (it?.data?.data ?? false) {
        let result = it.data.data;
        result = result.sort((a: any, b: any) => a.order - b.order);
        setCategoriesStatus(result);
      }
    })
  }

  const fetchRecommends = () => {
    recommends(3).then((it: any) => {
      if (it?.data?.data ?? false) {
        const shops = it.data.data.map((shop: any) => {
          return prepareShop(shop, locationStatus, locationService);
        });
        setRecommenedShops(shops);
      }
    })
  }

  const handleCategory = (categoryId: string) => {
    navigate(`/category/${categoryId}`);
  }

  return (
    <div className="home">
      <div className="container scrollable">
        <StaticSearchBox />
        <div className="categories">
          {categoriesStatus?.map((category: any, index: number) => (
            <div key={index} className="category" onClick={_ => handleCategory(category.key)}>
              <div
                className="image"
                style={{ backgroundImage: `url(${category.imageUrl})` }}
              />
              <div className="name">{category.name}</div>
            </div>
          ))}
        </div>
        { recommenededShops && recommenededShops!.length > 0 ? 
            <div className="recommend">
            <div className="header">이 가게 어때요?</div>
            <div className="shops">
            { recommenededShops.map((shop: any, index: number) => (
              <ShopItem key={index} shop={shop!} hideUnderbar={true} style={{padding: '1rem 0'}}/>  
            ))}</div>
          </div> : <></>
          }
      </div>
      <TabBar activeTab={0} />
    </div>
  )
}

export default Home
