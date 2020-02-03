# ArcTouchTest
Teste seletivo para vaga de Desenvolvedor Android pela empresa ArcTouch

Em resumo, o que foi implementado:

1. Criação do repositório no Github.
2. Atualização da versão do Kotlin de 1.3.10 para 1.3.31, e import do Kotlin no Gradle modificado de org.jetbrains.kotlin:kotlin-stdlib-jre7 para org.jetbrains.kotlin:kotlin-stdlib-jdk7.
3. Implementação da tela de Detalhes com os campos: name, poster image, backdrop image, list of genres, overview e release date.
4. Implementação de Paginação.
5. Criação de MainPresenter para abstrair lógica da Home.
6. Remoção da SplashScreen.
7. Implementação da busca por nome de filmes, parcial ou completa.
8. Remoção da BaseActivity.
9. Aprimoramento da manutenabilidade da camada de Network.
10. Tratamento de carregamento de dados após rotacionar a tela. Ou seja, implementar ViewModel e onSavedInstanceState aqui.
11. Mudança do ícone do aplicativo.
12. Aprimoramento do layout da DetailActivity usando CoordinatorLayout.
13. Criação do DetailPresenter.
14. Início de migração de métodos do MainPresenterImpl para o ViewModel.

E o que eu pretendia implementar se eu tivesse mais tempo:

15. Adicionar TextView para lista vazia.
16. Criar teste de JUnit.
17. Criar teste com Espresso.
18. Criar opção de menu para restaurar lista de upcoming.
19. Aplicar DataBinding.
20. Aplicar Dagger 2.
21. Aplicar PagingLibrary.
