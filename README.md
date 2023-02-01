# back

백엔드 레포지토리

| -           | -                     |
|-------------|-----------------------|
| Spring boot | 2.7.2                 |
| Java        | 11                    |
| Build tool  | maven                 |


### git commit message convention

- [Refactor] : 코드 리팩토링
- [Fix] : 버그 수정
- [Add] : 메서드, 클래스 추가
- [Remove] : 메서드, 클래스 삭제
- [Rename] : 메서드, 클래스 이름 변경
- [Structure] : 구조 변경

### Git branch 전략
- dev (개발환경)
- prd (운영환경)
- feature/${name} (기능 피처)

feature 브랜치 개발 후 dev PR -> prd 적용
