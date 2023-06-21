# 작업 Flow

## Linear 확인

Linear에 이슈가 등록되어 있는지 확인한다.

만약 등록되어 있지 않다면, 자신이 작업하고자 하는 것을 백로그의 sub-issue에 등록한다.

생성되는 RID 값을 확인한다.

## Github Issue 생성

자신이 진행하고자 하는 작업을 Issue에 등록한다.

Issue는 제공된 템플릿 중에서 선택하여 작성한다.

Issue에서 가장 중요한 것은

- Issue Number -> 이슈 관련 커밋 볼 수 있음
- RID Number -> linear 연동
- Issue Number & RID Number -> git branch 만들때 필요

## main pull

[중요!!!] main 브랜치에 지금까지 작업된 내용들을 local에 pull 한다.

항상 습관을 들이자.

## local branch 생성 및 진입

현재 main branch에 있다는 것을 가정한다.

[Issue Number]/[RID Number]/[type]/[title]

위와 같은 형식으로 local branch 를 생성한다.

```
git branch 10/rid-40/feat/firebase_setting
```

```
git checkout 10/rid-40/feat/firebase_setting
```

## 작업

작업 단위 별로 commit을 자주해주면서 작업한다.

commit 시에는 gitmessage template에 따라서, commit을 한다.

```
git add .
```

```
git commit
```

-> commit messeage 작성

```
git push origin 10/rid-40/feat/firebase_setting
```

## PR

이슈에 대한 작업이 완료되면 github에서 PR을 한다.

이때, local에서 작업한 브랜치가 원격저장소에 push를 했는지 확인한다.

PR은 PR template에 따라 진행한다.

## PR merge

PR 후, CI, 코드리뷰 등의 작업이 완료되면, main branch에 merge 한다.
