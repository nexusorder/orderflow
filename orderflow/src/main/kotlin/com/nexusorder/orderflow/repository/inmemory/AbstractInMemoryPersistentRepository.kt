package com.nexusorder.orderflow.repository.inmemory

import com.nexusorder.orderflow.model.storage.AbstractCoreModel
import org.reactivestreams.Publisher
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

// 재사용을 위한 추상 클래스 인메모리 레포지토리
@NoRepositoryBean
abstract class AbstractInMemoryPersistentRepository<T : AbstractCoreModel> : ReactiveCrudRepository<T, String> {
    protected val db = hashMapOf<String, T>()

    override fun findById(id: String): Mono<T> {
        return Mono.justOrEmpty(db[id])
    }

    override fun existsById(id: String): Mono<Boolean> {
        return Mono.just(db.containsKey(id))
    }

    override fun findById(id: Publisher<String>): Mono<T> {
        return Mono.from(id).map { db[it] }
    }

    override fun existsById(id: Publisher<String>): Mono<Boolean> {
        return Mono.from(id).map { db.containsKey(it) }
    }

    override fun <S : T> save(entity: S): Mono<S> {
        return Mono.fromCallable {
            db[entity.id] = entity
            entity
        }
    }

    override fun <S : T> saveAll(entities: MutableIterable<S>): Flux<S> {
        return Flux.fromIterable(entities)
            .map {
                db[it.id] = it
                it
            }
    }

    override fun <S : T> saveAll(entityStream: Publisher<S>): Flux<S> {
        return Flux.from(entityStream)
            .map {
                db[it.id] = it
                it
            }
    }

    override fun findAll(): Flux<T> {
        return Flux.fromIterable(db.values)
    }

    override fun findAllById(idStream: Publisher<String>): Flux<T> {
        return Flux.from(idStream)
            .map { db[it] }
    }

    override fun findAllById(ids: MutableIterable<String>): Flux<T> {
        return Flux.fromIterable(ids)
            .map { db[it] }
    }

    override fun deleteById(id: String): Mono<Void> {
        return Mono.fromCallable {
            db.remove(id)
            null
        }
    }

    override fun deleteAll(): Mono<Void> {
        return Mono.fromCallable {
            db.clear()
            null
        }
    }

    override fun count(): Mono<Long> {
        return Mono.just(db.size.toLong())
    }

    override fun deleteAllById(ids: MutableIterable<String>): Mono<Void> {
        return Mono.fromCallable {
            ids.forEach { db.remove(it) }
            null
        }
    }

    override fun delete(entity: T): Mono<Void> {
        return Mono.fromCallable {
            db.remove(entity.id)
            null
        }
    }

    override fun deleteAll(entityStream: Publisher<out T>): Mono<Void> {
        return Mono.from(entityStream)
            .doOnNext {
                db.remove(it.id)
            }.then()
    }

    override fun deleteAll(entities: MutableIterable<T>): Mono<Void> {
        return Flux.fromIterable(entities).doOnNext {
            db.remove(it.id)
        }.then()
    }

    override fun deleteById(id: Publisher<String>): Mono<Void> {
        return Mono.from(id)
            .doOnNext {
                db.remove(it)
            }.then()
    }
}
