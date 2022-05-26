package apiserver.apiserver.helper;

import apiserver.apiserver.exception.CannotConvertNestedStructureException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/*
우리는 요구 사항에 의해, 카테고리 뿐만 아니라 대댓글도 계층형으로 만들어야합니다.
이를 위해 계층형 구조 변환을 도와주는 헬퍼 클래스를 별도로 작성한 것이고,
대댓글 기능을 구현할 때도 재사용할 수 있을 것입니다.
 */
public class NestedConvertHelper <K, E, D> {
    /*
    K : 엔티티의 key 타입
    E : 엔티티의 타입
    D : 엔티티가 변환된 DTO의 타입
     */

    /*
    우리는 기존에 작성된 엔티티와 DTO는 전혀 건드리지 않을 것입니다.
    엔티티가 변환되어야 할 DTO 타입은, 이미 엔티티에 대해서 알고 있습니다.
    엔티티의 수정 없이 entities를 계층형 DTO 구조로 변환하기 위해,
    필요한 함수들을 DTO를 통해서 주입받아서 사용할 것입니다.
     */
    private List<E> entities;
    private Function<E, D> toDto;
    private Function<E, E> getParent;
    private Function<E, K> getKey;
    private Function<D, List<D>> getChildren;

    public static <K, E, D> NestedConvertHelper newInstance(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        return new NestedConvertHelper<K, E, D>(entities, toDto, getParent, getKey, getChildren);
    }

    private NestedConvertHelper(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        this.entities = entities;
        this.toDto = toDto;
        this.getParent = getParent;
        this.getKey = getKey;
        this.getChildren = getChildren;
    }

    public List<D> convert() {
        try {
            return convertInternal();
        } catch (NullPointerException e) {
            throw new CannotConvertNestedStructureException(e.getMessage());
        }
    }

    private List<D> convertInternal() {
        Map<K, D> map = new HashMap<>();
        List<D> roots = new ArrayList<>();

        for (E e : entities) {
            D dto = toDto(e);
            map.put(getKey(e), dto);
            if (hasParent(e)) {
                E parent = getParent(e);
                K parentKey = getKey(parent);
                D parentDto = map.get(parentKey);
                getChildren(parentDto).add(dto);
            } else {
                roots.add(dto);
            }
        }
        return roots;
    }

    private boolean hasParent(E e) {
        return getParent(e) != null;
    }

    private E getParent(E e) {
        return getParent.apply(e);
    }

    private D toDto(E e) {
        return toDto.apply(e);
    }

    private K getKey(E e) {
        return getKey.apply(e);
    }

    private List<D> getChildren(D d) {
        return getChildren.apply(d);
    }
}
