import axios, { CancelTokenSource } from 'axios'
import { createRef, useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useRecoilState } from 'recoil'
import '../asset/css/Search.css'
import SearchBox from '../component/SearchBox'
import ShopItem from '../component/ShopItem'
import TabBar from '../component/TabBar'
import { categories } from '../service/CategoryService'
import locationService, { locationAtom } from '../service/LocationService'
import { prepareShop, search } from '../service/ShopService'

function Search({ type = 'search' }: { type: string }) {
  
  const [shops, setShops] = useState([])
  const [keyword, setKeyword] = useState('')
  const [locationStatus, _] = useRecoilState(locationAtom)
  const [categoriesStatus, setCategoriesStatus] = useState([])
  const [selectedCategory, setSelectedCategory] = useState('')
  
  const [categoryRefs, setCategoryRefs] = useState<{ [key: string]: React.RefObject<HTMLDivElement> }>({});

  const navigate = useNavigate();
  const location = useLocation();

  let cancelTokenSource: CancelTokenSource | undefined;

  useEffect(() => {
    if (type === 'search') {
      location.state?.keyword && setKeyword(location.state.keyword);
    }
  }, []);

  useEffect(() => {
    if (type === 'category') {
      const category = window.location.pathname.split('/').pop() ?? ''
      setSelectedCategory(category);
      fetchCategories().then((_) => {
        return requestSearch({ category })
      })
    }
  }, [selectedCategory])

  useEffect(() => {
    setCategoryRefs(
      categoriesStatus.reduce((acc: { [key: string]: React.RefObject<HTMLDivElement> }, category: any) => {
        acc[category.key] = createRef<HTMLDivElement>();
        return acc;
      }, {})
    );
  }, [categoriesStatus]);

  useEffect(() => {
    if (selectedCategory) {
      categoryRefs[selectedCategory]?.current?.scrollIntoView({ behavior: 'auto' });
    }
  }, [categoryRefs, selectedCategory]);

  const requestSearch = (request: any, source?: CancelTokenSource) => {
    return search(request, source).then((it: any) => {
      if (it?.data?.data ?? false) {
        let mutated = it.data.data
        mutated = mutated.map((shop: any) => {
          return prepareShop(shop, locationStatus, locationService);
        })
        setShops(mutated)
      }
    }).catch((error: any) => {
      if (!axios.isCancel(error)) {
        console.log('%cSearch error', "color: skyblue;", error.message);
      }
    })
  }

  const fetchCategories = () => {
    return categories().then((it: any) => {
      if (it?.data?.data ?? false) {
        let result = it.data.data
        result = result.sort((a: any, b: any) => a.order - b.order)
        setCategoriesStatus(result)
      }
    })
  }

  const onSearchKeyUp = (e: any) => {
    const targetKeyword = e.target?.value?.trim() ?? '';
    if (targetKeyword === '') {
      return setShops([]);
    }

    if (cancelTokenSource) {
      cancelTokenSource.cancel('');
      cancelTokenSource = undefined;
    }
  
    cancelTokenSource = axios.CancelToken.source();
    return requestSearch({ name: targetKeyword }, cancelTokenSource)
    .then(() => {
      cancelTokenSource = undefined;
    });
  }

  const handleCategory = (categoryId: string) => {
    if (categoryId === selectedCategory) {
      return;
    }
    navigate(`/category/${categoryId}`, {state: {key: {from: 'search', keyword: keyword}}});
    setSelectedCategory(categoryId)
  }

  return (
    <div className="search">
      {type === 'search' ? (
        <SearchBox
          className="keyword"
          placeholder="먹고 싶은 메뉴나 가게를 찾아보세요"
          value={keyword}
          setValue={setKeyword}
          onKeyUp={onSearchKeyUp}
        />
      ) : (
        <div className="category-filter scrollable">
          <div className="category-wrapper">
            {categoriesStatus?.map((category: any, index: number) => (
              <div 
                key={index} 
                className={"category" + (category.key === selectedCategory ? ' selected' : '')} 
                onClick={_ => handleCategory(category.key)}
                ref={categoryRefs[category.key]!}
                style={{ cursor: (category.key !== selectedCategory ? 'pointer' : 'auto') }}
              >
                <div className="name">{category.name}</div>
              </div>
            ))}
          </div>
        </div>
      )}
      <div className="container scrollable">
        <div className="shops">
          {shops?.map((shop: any, index: number) => (
            <ShopItem key={index} shop={shop} />
          )) ?? <></>}
        </div>
      </div>
      <TabBar activeTab={1} />
    </div>
  )
}

export default Search
