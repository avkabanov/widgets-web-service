# Widgets Web Service
a web service that allows to work with widgets via HTTP REST API.

## API documentation
All calls to API must be started with http://localhost:8080/

Application exposes following REST endpoints

| Http method | Endpoint               | Returns                            | Description                                                                |
|-------------|------------------------|------------------------------------|----------------------------------------------------------------------------|
| POST        | /widget/create         | Created widget                     | Creates new widget. Created of widgets returned                            |
| GET         | /widget/{uuid}         | Widget with the given ID           | Retrieves widget with the given ID                                         |
| GET         | /widget/all            | All widgets                        | Retrieves all widgets                                                      |
| GET         | /widget/all/paged/?pageNumber={page_number}&pageSize={page_size}     | All widgets with pagination        | Retrieves all widgets with pagination. Page has zero-based page index.     |
| PUT         | /widget/update         | Updated widget                     | Updates the widget                                                         |
| GET         | /widget/filter         | Widgets that fall into the region  | Return only the widgets that fall entirely into the region                 |
| DELETE      | /widget/delete/{uuid}  | Removes widget with the given ID   |                                                                            |

## Structure overview 
created widgets can be stored in local memory or in database. Implementation of `com.kabanov.widgets.dao.WidgetCache` specify where widgets will be stored. 

Currently there are two implementatons: 
 - `com.kabanov.widgets.dao.cache.InMemoryWidgetCache` stores widgets in local memory
 - `com.kabanov.widgets.dao.db.DatabaseWidgetCache` stored widgets in database
 
 Implementation can be choosen by activating profile in `application.properties`
 
- `spring.profiles.active=databaseStorage`
or
- `spring.profiles.active=inMemoryStorage`

### in Memory Storage
In order to optimize access to widgets, three classes to store widgets has been used: 

#### com.kabanov.widgets.dao.cache.WidgetCache 
stores widgets in ConcurrentHashMap what allows to access widgets by id in a constant time.

#### com.kabanov.widgets.dao.cache.WidgetLayersStorage 
contains logic of setting proper Z index, and shifting widgets with the same Z index. Under the hood all widgets
stored in ConcurrentSkipListSet what allows to add and remove different widgets simultaneously. 

There is also additional synchronization was added: in the process of inserting widget with existing z-index, 
tree is rebuild. In that case no read-remove operations allowed because tree data is inconsistent. 

To achieve that synchronization I used ReentrantReadWriteLock. 
 - readLock can be acquired by multiple threads and required for operations like read/add/remove
 - writeLock and be acquired only by single thread and it is used to restrict tree access while tree is rebuild.
 
Due to the tree structure, we can get all widgets sorted by Z-layer in a constant time     

#### com.kabanov.widgets.dao.cache.WidgetPositionStorage 
Stores all widgets sorted by their start point. 
That gives performance when it's required to find all widgets that fall into the region. 

By iterating over sorted by start-point widgets, if start point (bottom-left point) of some widget is after top-right
point of a region - that means this and all following widgets will not fall into the region. 

Therefore we don't need to check all widgets if they fall into region or not.    

### Database storage
#### get all widgets that fall into the region
It has been decided to use the same tree-base approach: by iterating over sorted by start-point widgets, if start point (bottom-left point) of some widget is after top-right point of a region - that means this and all following widgets will not fall into the region.

In order to have an ability to iterate over sorted by start-point widgets, special column was added to the entity: `startPointSum` and index on that column.

Using paging mechanism, we can receive sorted widgets one by one from database. We stop iterating when we find a widget, that gives a guarantee that all other widgets will be out of the region

## Pagination

## Rate Limiting
